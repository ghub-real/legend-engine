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

package org.finos.legend.engine.plan.execution.stores.document.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.Span;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.finos.legend.engine.plan.dependencies.store.document.DocumentResultSet;
import org.finos.legend.engine.plan.dependencies.store.document.DocumentResultSetImpl;
import org.finos.legend.engine.plan.execution.result.ExecutionActivity;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.plan.execution.result.ResultVisitor;
import org.finos.legend.engine.plan.execution.stores.document.activity.NonRelationalExecutionActivity;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.DocumentQueryExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result.DocumentQueryResultField;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result.ResultField;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DocumentQueryExecutionResult extends Result
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Alloy Execution Server");
    private final String databaseType;
    private final String databaseTimeZone;
    private final Calendar calendar;
    private final DocumentResultSet resultSet;
    private final NonRelationalClient client;
    // private final List<String> temporaryTables;
//  SQL specific stuff
//    private final Connection connection;
//    private final Statement statement;
//    private final ResultSetMetaData resultSetMetaData;
    private final String executedMql;
    //private final int fieldCount;
    private final List<String> fieldNames = FastList.newList();
    // Simple object with mapping of model datatype  & db data type
    private final List<ResultField> resultFields = FastList.newList();
    //From protocol - execution node.
    private final List<DocumentQueryResultField> documentQueryResultFields = FastList.newList();
    public Span topSpan;
    DocumentQueryExecutionNode documentQueryExecutionNode;

    public DocumentQueryExecutionResult(List<ExecutionActivity> activities, DocumentQueryExecutionNode executionNode, String databaseType, String databaseTimeZone, NonRelationalClient nonRelationalClient, MutableList<CommonProfile> profiles, List<String> temporaryTables, Span topSpan)
    {
        super("success", activities);


        this.documentQueryExecutionNode = executionNode;
        this.databaseType = databaseType;
        this.databaseTimeZone = databaseTimeZone;
        this.calendar = new GregorianCalendar(TimeZone.getTimeZone(databaseTimeZone));

        this.client = nonRelationalClient;
        String nativeQL = ((NonRelationalExecutionActivity) activities.get(activities.size() - 1)).sql;
        this.executedMql = nativeQL;
        this.resultSet = new DocumentResultSetImpl(nonRelationalClient.executeNativeQuery(nativeQL));

        this.topSpan = topSpan;
    }

    @Override
    public <T> T accept(ResultVisitor<T> resultVisitor)
    {
        return ((DocumentStoreResultVisitor<T>) resultVisitor).visit(this);
    }

    public DocumentQueryExecutionNode getDocumentQueryExecutionNode()
    {
        return this.documentQueryExecutionNode;
    }

    public String getDatabaseType()
    {
        return this.databaseType;
    }

    public String getDatabaseTimeZone()
    {
        return this.databaseTimeZone;
    }

//    public List<String> getTemporaryTables()
//    {
//        return temporaryTables;
//    }
//
//    public Connection getConnection()
//    {
//        return this.connection;
//    }
//
//    public Statement getStatement()
//    {
//        return this.statement;
//    }
//
    public DocumentResultSet getResultSet()
    {
        return this.resultSet;
    }
//
//    public ResultSetMetaData getResultSetMetaData()
//    {
//        return this.resultSetMetaData;
//    }
//
//    public int getColumnCount()
//    {
//        return this.columnCount;
//    }

    public List<DocumentQueryResultField> getDocumentQueryResultFields()
    {
        return documentQueryResultFields;
    }

    public Span getTopSpan()
    {
        return topSpan;
    }

    public List<String> getFieldNames()
    {
        return this.fieldNames;
    }

    public List<ResultField> getResultFields()
    {
        return this.resultFields;
    }

    public String getExecutedMql()
    {
        return this.executedMql;
    }

    public Object getTransformedValue(int columnIndex)
    {
        ResultField resultField = this.getResultFields().get(columnIndex - 1);
        ObjectMapper mapper = new ObjectMapper();
        return resultField.getTransformedValue(mapper.valueToTree(this.getDocumentQueryResultFields()), calendar);
    }

    @Override
    public void close()
    {
//        DatabaseManager databaseManager = DatabaseManager.fromString(this.SQLExecutionNode.getDatabaseTypeName());
//        if (this.temporaryTables != null && this.statement != null)
//        {
//            this.temporaryTables.forEach((Consumer<? super String>) table ->
//            {
//                try
//                {
//                    statement.execute(databaseManager.relationalDatabaseSupport().dropTempTable(table));
//                }
//                catch (Exception ignored)
//                {
//                }
//            });
//        }
//
//        Consumer<AutoCloseable> closingFunction = (AutoCloseable c) ->
//        {
//            if (c != null)
//            {
//                try
//                {
//                    c.close();
//                }
//                catch (Exception ignored)
//                {
//                }
//            }
//        };
//
//        FastList.newListWith(this.resultSet, this.statement, this.connection).forEach((Procedure<AutoCloseable>) closingFunction::accept);
    }

}