// Copyright 2021 Goldman Sachs
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

package org.finos.legend.engine.plan.execution.stores.document.result;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.finos.legend.engine.plan.execution.result.ExecutionActivity;
import org.finos.legend.engine.plan.execution.result.ResultVisitor;
import org.finos.legend.engine.plan.execution.result.StreamingResult;
import org.finos.legend.engine.plan.execution.result.builder.Builder;
import org.finos.legend.engine.plan.execution.result.serialization.SerializationFormat;
import org.finos.legend.engine.plan.execution.result.serialization.Serializer;
import org.finos.legend.engine.plan.execution.stores.relational.result.RelationalResult;
import org.finos.legend.engine.plan.execution.stores.relational.result.RelationalResultVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result.DocumentQueryResultField;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.model.result.SQLResultColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RealizedNonRelationalResult extends StreamingResult
{
    public Builder builder;
    public List<ExecutionActivity> activities;
    public List<DocumentQueryResultField> fields;
    public List<List<Object>> resultSetRows;
    public List<List<Object>> transformedRows;

    private static final int DEFAULT_ROW_LIMIT = 1000;
    public static final String ROW_LIMIT_PROPERTY_NAME = "org.finos.legend.engine.realizedNonRelationalResultRowLimit";

    public RealizedNonRelationalResult(NonRelationalResult nonRelationalResult) throws SQLException
    {
        super(nonRelationalResult.activities);
        this.builder = nonRelationalResult.builder;
        this.fields = nonRelationalResult.getSQLResultColumns();
        int fieldCount = this.fields.size();

        this.transformedRows = Lists.mutable.empty();
        this.resultSetRows = Lists.mutable.empty();
        //ResultSet resultSet = nonRelationalResult.resultSet;
        int SUPPORTED_RESULT_ROWS = getRowLimit();
        int rowCount = 0;
        try
        {
//            while (resultSet.next())
//            {
//                if (rowCount > SUPPORTED_RESULT_ROWS)
//                {
//                    throw new RuntimeException("Too many rows returned. Realization of relational results currently supports results with up to " + SUPPORTED_RESULT_ROWS + " rows.");
//                }
//
//                List<Object> transformedRow = Lists.mutable.empty();
//                List<Object> resultSetRow = Lists.mutable.empty();
//                MutableList<Function<Object, Object>> transformers = nonRelationalResult.getTransformers();
//                for (int i = 1; i <= fieldCount - 1; i++)
//                {
//                    transformedRow.add(transformers.get(i - 1).valueOf(nonRelationalResult.getValue(i)));
//                    resultSetRow.add(nonRelationalResult.getValue(i));
//                }
//                transformedRow.add(transformers.get(fieldCount - 1).valueOf(nonRelationalResult.getValue(fieldCount)));
//                resultSetRow.add(nonRelationalResult.getValue(fieldCount));
//                transformedRows.add(transformedRow);
//                resultSetRows.add(resultSetRow);
//                rowCount += 1;
//            }
        }
        finally
        {
            nonRelationalResult.close();
        }
    }

    public int getRowLimit()
    {
        return Integer.getInteger(ROW_LIMIT_PROPERTY_NAME, DEFAULT_ROW_LIMIT);
    }

    private RealizedNonRelationalResult()
    {
        super(Lists.mutable.empty());
    }

    @Override
    public <T> T accept(ResultVisitor<T> resultVisitor)
    {
        return ((RelationalResultVisitor<T>) resultVisitor).visit(this);
    }


    public static RealizedNonRelationalResult emptyRealizedRelationalResult(List<DocumentQueryResultField> documentQueryResultFields)
    {
        RealizedNonRelationalResult realizedRelationalResult = new RealizedNonRelationalResult();
        realizedRelationalResult.fields = documentQueryResultFields;
        realizedRelationalResult.transformedRows = Lists.mutable.empty();
        realizedRelationalResult.resultSetRows = Lists.mutable.empty();

        return realizedRelationalResult;
    }

    public void addRow(List<Object> resultSetRow, List<Object> transformedRow)
    {
        this.resultSetRows.add(resultSetRow);
        this.transformedRows.add(transformedRow);
    }

    public List<Map<String, Object>> getRowValueMaps(boolean withTransform)
    {
        List<Map<String, Object>> rowValueMaps = Lists.mutable.empty();
        (withTransform ? this.transformedRows : this.resultSetRows).forEach(row ->
        {
            Map<String, Object> rowValMap = Maps.mutable.empty();
            int index = 0;
            for (DocumentQueryResultField col : this.fields)
            {
                rowValMap.put("", row.get(index));
                index += 1;
            }
            rowValueMaps.add(rowValMap);
        });
        return rowValueMaps;
    }

    @Override
    public Builder getResultBuilder()
    {
        return this.builder;
    }

    @Override
    public Serializer getSerializer(SerializationFormat format)
    {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}