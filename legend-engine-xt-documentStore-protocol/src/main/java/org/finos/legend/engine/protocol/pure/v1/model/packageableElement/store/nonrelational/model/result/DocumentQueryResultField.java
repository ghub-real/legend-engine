// Copyright 2020 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

public class DocumentQueryResultField
{
    public String label;
    public String dataType;

    public DocumentQueryResultField()
    {
        // DO NOT DELETE: this resets the default constructor for Jackson
    }

    public DocumentQueryResultField(DocumentQueryResultField that)
    {
        this.label = that.label;
        this.dataType = that.dataType;
    }

    public DocumentQueryResultField(String label, String dataType)
    {
        this.label = label;
        this.dataType = dataType;
    }

    @JsonIgnore
    public String getNonQuotedLabel()
    {
        return this.label.startsWith("\"") && this.label.endsWith("\"") ? this.label.substring(1, this.label.length() - 1) : this.label;
    }

    @JsonIgnore
    public String getQuotedLabelIfContainSpace()
    {
        String nonQuotedLabel = this.getNonQuotedLabel();
        return nonQuotedLabel.contains(" ") ? "\"" + nonQuotedLabel + "\"" : nonQuotedLabel;
    }

    @JsonIgnore
    public Pair<String, String> labelTypePair()
    {
        if (this.dataType == null)
        {
            return Tuples.pair(this.label, "String"); //TODO: This should not be null. Change after all relational types are available
        }
        String type = this.dataType.toUpperCase();
        if (type.startsWith("STRING"))
        {
            return Tuples.pair(this.label, "String");
        }
        else
        {
            if (type.startsWith("DOUBLE"))
            {
                return Tuples.pair(this.label, "Double");
            }
            else
            {
                if (type.startsWith("DECIMAL"))
                {
                    return Tuples.pair(this.label, "Decimal");
                }
                else
                {
                    if (type.startsWith("INTEGER"))
                    {
                        return Tuples.pair(this.label, "Integer");
                    }
                    else
                    {
                        if (type.startsWith("LONG"))
                        {
                            return Tuples.pair(this.label, "Long");
                        }
                        else
                        {
                            if (type.startsWith("Boolean"))
                            {
                                return Tuples.pair(this.label, "Boolean");
                            }
                            else
                            {
                                if (type.startsWith("TIMESTAMP") || type.startsWith("DATE"))
                                {
                                    return Tuples.pair(this.label, "DateTime");
                                }
                                else
                                {
                                    if (type.startsWith("OBJECTTYPE"))
                                    {
                                        return Tuples.pair(this.label, "ObjectType");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return Tuples.pair(this.label, "String"); // Default is String. But shouldn't go here
    }
}
