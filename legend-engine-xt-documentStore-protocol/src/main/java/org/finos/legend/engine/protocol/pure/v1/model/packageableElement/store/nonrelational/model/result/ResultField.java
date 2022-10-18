// Copyright 2022 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.function.BiFunction;

public class ResultField
{

    //private int columnIndex;
    private String label;
    private String dataType;
    //private int dbMetaDataType;

    private BiFunction<JsonNode, Calendar, Object> valueExtractor;
    private BiFunction<JsonNode, Calendar, Object> transformedValueExtractor;

    ResultField(int columnIndex, String label, String dataType, String nodeType)
    {
        this.label = label;
        this.dataType = dataType;

        this.createValueExtractors();
    }

    private void createValueExtractors()
    {
        /* Value Extractor */
        if ("DATE".equalsIgnoreCase(this.dataType))
        {
            this.valueExtractor = (rootNode, calendar) -> LocalDate.parse(rootNode.get(this.label).asText());

        }
        else
        {
            if ("TIMESTAMP".equalsIgnoreCase(this.dataType))
            {
                this.valueExtractor = (rootNode, calendar) -> LocalDateTime.parse(rootNode.get(this.label).asText());
            }
            else
            {
                this.valueExtractor = (rootNode, calendar) -> rootNode.get(this.label);
            }
        }


        this.transformedValueExtractor = (rootNode, calendar) ->
        {
            JsonNode valueNode = rootNode.path(this.label);


            switch (valueNode.getNodeType())
            {
                case BOOLEAN:
                    if (valueNode.isBoolean())
                    {
                        return valueNode.asBoolean();
                    }
                    break;
                case NUMBER:
                    if (valueNode.isInt() || valueNode.isLong())
                    {
                        return valueNode.asLong();
                    }
                    else
                    {
                        if (valueNode.isDouble() || valueNode.isFloat())
                        {
                            return valueNode.asDouble();
                        }
                    }
                    break;
                case OBJECT:
                    return valueNode;

                case NULL:
                    return "NULL";

                case BINARY:
                    try
                    {
                        byte[] bytes = valueNode.get(this.label).binaryValue();
                        return bytes != null ? BinaryUtils.encodeHex(bytes) : null;
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                default:
                    return valueNode.get(this.label).asText();
            }
            return valueNode.get(this.label).asText();
        };

    }

    public String getLabel()
    {
        return this.label;
    }

    public String getNonQuotedLabel()
    {
        return this.label.startsWith("\"") && this.label.endsWith("\"") ? this.label.substring(1, this.label.length() - 1) : this.label;
    }

    public String getDataType()
    {
        return this.dataType;
    }


    public Object getValue(JsonNode node, Calendar calendar)
    {
        return this.valueExtractor.apply(node, calendar);
    }

    public Object getTransformedValue(JsonNode node, Calendar calendar)
    {
        return this.transformedValueExtractor.apply(node, calendar);
    }

    @JsonIgnore
    public Pair<String, String> labelTypePair()
    {
        if (this.dataType == null)
        {
            return Tuples.pair(this.label, "String"); //TODO: This should not be null. Change after all relational types are available
        }
        String type = this.dataType.toUpperCase();
        if (type.startsWith("VARCHAR") || type.startsWith("CHAR"))
        {
            return Tuples.pair(this.label, "String");
        }
        if (type.startsWith("FLOAT") || type.startsWith("DOUBLE") || type.startsWith("DECIMAL") || type.startsWith("NUMERIC") || type.startsWith("REAL"))
        {
            return Tuples.pair(this.label, "Float");
        }
        if (type.startsWith("INTEGER") || type.startsWith("BIGINT") || type.startsWith("SMALLINT") || type.startsWith("TINYINT"))
        {
            return Tuples.pair(this.label, "Integer");
        }
        if (type.startsWith("BIT"))
        {
            return Tuples.pair(this.label, "Boolean");
        }
        if (type.startsWith("TIMESTAMP"))
        {
            return Tuples.pair(this.label, "DateTime");
        }
        if (type.startsWith("DATE"))
        {
            return Tuples.pair(this.label, "StrictDate");
        }
        return Tuples.pair(this.label, "String"); // Default is String. But shouldn't go here
    }
}
