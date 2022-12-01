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

package org.finos.legend.engine.plan.execution.stores.document;

import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.finos.legend.engine.plan.execution.nodes.helpers.freemarker.FreeMarkerExecutor;
import org.finos.legend.engine.plan.execution.nodes.state.ExecutionState;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.plan.execution.stores.StoreType;
import org.finos.legend.engine.plan.execution.stores.document.activity.NonRelationalExecutionActivity;
import org.finos.legend.engine.plan.execution.stores.document.config.NonRelationalExecutionConfiguration;
import org.finos.legend.engine.plan.execution.stores.document.config.TemporaryTestDbConfiguration;
import org.finos.legend.engine.plan.execution.stores.document.connection.manager.ConnectionManagerSelector;
import org.finos.legend.engine.plan.execution.stores.document.plugin.NonRelationalStoreExecutionState;
import org.finos.legend.engine.plan.execution.stores.document.result.DocumentQueryExecutionResult;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.DocumentQueryExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection;
import org.finos.legend.engine.shared.core.operational.logs.LogInfo;
import org.finos.legend.engine.shared.core.operational.logs.LoggingEventType;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class NonRelationalExecutor
{
    public static final String DEFAULT_DB_TIME_ZONE = "GMT";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Alloy Execution Server");
    private final ConnectionManagerSelector connectionManager;
    private final NonRelationalExecutionConfiguration nonRelationalExecutionConfiguration;
//    private MutableList<Function2<ExecutionState, List<Map<String, Object>>, Result>> resultInterpreterExtensions;

//    private static final MutableMap<String, String> DATA_TYPE_RELATIONAL_TYPE_MAP = Maps.mutable.empty();

//    static
//    {
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("Integer", "INT");
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("Float", "FLOAT");
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("Number", "FLOAT");
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("String", "VARCHAR(1000)");
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("Date", "TIMESTAMP");
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("DateTime", "TIMESTAMP");
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("StrictDate", "DATE");
//        DATA_TYPE_RELATIONAL_TYPE_MAP.put("Boolean", "BIT");
//
//    }

    //private Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder;

//    public NonRelationalExecutor(TemporaryTestDbConfiguration temporarytestdb, NonRelationalExecutionConfiguration nonRelationalExecutionConfiguration)
//    {
//        this(temporarytestdb, nonRelationalExecutionConfiguration);
//    }

    public NonRelationalExecutor(TemporaryTestDbConfiguration temporarytestdb, NonRelationalExecutionConfiguration nonRelationalExecutionConfiguration) //removed Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder
    {
        //this.flowProviderHolder = flowProviderHolder;
        this.connectionManager = new ConnectionManagerSelector(temporarytestdb, nonRelationalExecutionConfiguration.oauthProfiles, Optional.empty());
        this.nonRelationalExecutionConfiguration = nonRelationalExecutionConfiguration;
//        this.resultInterpreterExtensions = Iterate.addAllTo(ServiceLoader.load(ResultInterpreterExtension.class), Lists.mutable.empty()).collect(ResultInterpreterExtension::additionalResultBuilder);
    }

    public NonRelationalExecutionConfiguration getNonRelationalExecutionConfiguration()
    {
        return this.nonRelationalExecutionConfiguration;
    }

    public ConnectionManagerSelector getConnectionManager()
    {
        return this.connectionManager;
    }


    public Result execute(DocumentQueryExecutionNode node, MutableList<CommonProfile> profiles, ExecutionState executionState)
    {
        LOGGER.info("Executing against MongoDB");

        String databaseTimeZone = node.getDatabaseTimeZone() == null ? DEFAULT_DB_TIME_ZONE : node.getDatabaseTimeZone();
        String databaseType = node.getDatabaseTypeName();
        List<String> tempTableList = FastList.newList();

        Span span = GlobalTracer.get().activeSpan();

        // TODO: goncah we shouldn't need to cast and we should follow similar pattern with relational using DatabaseManager, ConnectionManager, DataSourceKey etc.
        NonRelationalClient nonRelationalClient = getClient(node, profiles, (NonRelationalStoreExecutionState) executionState.getStoreExecutionState(StoreType.NonRelational));
        if (span != null)
        {
            span.log("Non-Relational Client acquired");
        }

        this.prepareForQueryExecution(node, nonRelationalClient, databaseTimeZone, databaseType, profiles, executionState);

//        DataStoreConnection databaseConnection = (DataStoreConnection) node.connection;
//        MongoDBDatasourceSpecification datasourceSpecification = (MongoDBDatasourceSpecification) databaseConnection.datasourceSpecification;
//        List<String> results = Lists.mutable.empty();
//        LocalMongoDBClient mongoDBClient = null;
//        String query = node.getQuery();
//
//        if (!Objects.equals(query, "") && query != null)
//        {
//            try
//            {
//                mongoDBClient = new LocalMongoDBClient(datasourceSpecification);
//                results = mongoDBClient.executeCustomAggregationQueryToDefaultDB(query);
//            }
//            catch (Exception e)
//            {
//                LOGGER.error(e.toString());
//            }
//            finally
//            {
//                if (mongoDBClient != null)
//                {
//                    mongoDBClient.close();
//                }
//            }
//
//        }

        return new DocumentQueryExecutionResult(executionState.activities, node, databaseType, databaseTimeZone, nonRelationalClient, profiles, tempTableList, executionState.topSpan);
    }

    private void prepareForQueryExecution(DocumentQueryExecutionNode node, NonRelationalClient nonRelationalClient, String databaseTimeZone, String databaseTypeName, MutableList<CommonProfile> profiles, ExecutionState executionState)
    {
        String documentQuery;

        documentQuery = node.getQuery(); // returns mongoQuery

//        DatabaseManager databaseManager = DatabaseManager.fromString(databaseTypeName);
//        for (Map.Entry<String, Result> var : executionState.getResults().entrySet())
//        {
//            if (var.getValue() instanceof StreamingResult && sqlQuery.contains("(${" + var.getKey() + "})"))
//            {
//                String tableName = databaseManager.relationalDatabaseSupport().processTempTableName(var.getKey());
//                this.prepareTempTable(connection, (StreamingResult) var.getValue(), tableName, databaseTypeName, databaseTimeZone, tempTableList);
//                tempTableList.add(tableName);
//                sqlQuery = sqlQuery.replace("(${" + var.getKey() + "})", tableName);
//            }
//            else if (var.getValue() instanceof PreparedTempTableResult && sqlQuery.contains("(${" + var.getKey() + "})"))
//            {
//                sqlQuery = sqlQuery.replace("(${" + var.getKey() + "})", ((PreparedTempTableResult) var.getValue()).getTempTableName());
//            }
//            else if (var.getValue() instanceof RelationalResult && (sqlQuery.contains("inFilterClause_" + var.getKey() + "})") || sqlQuery.contains("${" + var.getKey() + "}")))
//                {
//                    if (((RelationalResult) var.getValue()).columnCount == 1)
//                    {
//                        RealizedRelationalResult realizedRelationalResult = (RealizedRelationalResult) var.getValue().realizeInMemory();
//                        List<Map<String, Object>> rowValueMaps = realizedRelationalResult.getRowValueMaps(false);
//                        executionState.addResult(var.getKey(), new ConstantResult(rowValueMaps.stream().flatMap(map -> map.values().stream()).collect(Collectors.toList())));
//                    }
//                }
//        }

        if (documentQuery == null)
        {
            throw new RuntimeException("NonRelation query does not exist in DocumentQueryExecutionNode(?)");
        }

        try
        {
            documentQuery = FreeMarkerExecutor.process(documentQuery, executionState, databaseTypeName, databaseTimeZone);
            Span span = GlobalTracer.get().activeSpan();
            if (span != null)
            {
                span.setTag("generatedDocumentQuery", documentQuery);
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Reprocessing document failed with vars " + executionState.getResults().keySet(), e);
        }

        LOGGER.info(new LogInfo(profiles, LoggingEventType.EXECUTION_NONRELATIONAL_REPROCESS_SQL, "Reprocessing sql with vars " + executionState.getResults().keySet() + ": " + documentQuery).toString());

        executionState.activities.add(new NonRelationalExecutionActivity(documentQuery));
    }


    private NonRelationalClient getClient(DocumentQueryExecutionNode node, MutableList<CommonProfile> profiles, NonRelationalStoreExecutionState executionState)
    {
        return this.getClient(node.connection, node.onConnectionCloseRollbackQuery, node.onConnectionCloseCommitQuery, profiles, executionState);
    }


    private NonRelationalClient getClient(DatabaseConnection databaseConnection, String onConnectionCloseRollbackQuery, String onConnectionCloseCommitQuery, MutableList<CommonProfile> profiles, NonRelationalStoreExecutionState executionState)
    {
        return executionState.getNonRelationalExecutor().getConnectionManager().getDatabaseConnection(profiles, databaseConnection, executionState.getRuntimeContext());
    }

//    public static String process(String query, Map<?, ?> vars, String templateFunctions)
//    {
//        String result = "";
//        try
//        {
//            Configuration cfg = new Configuration();
//            cfg.setNumberFormat("computer");
//            Template t = new Template("sqlTemplate", new StringReader(templateFunctions + "\n" + query), cfg);
//            StringWriter stringWriter = new StringWriter();
//            t.process(vars, stringWriter);
//            result = stringWriter.toString();
//        }
//        catch (Exception ignored)
//        {
//        }
//        return result;
//    }
//
//    public static String getRelationalTypeFromDataType(String dataType)
//    {
//        return DATA_TYPE_RELATIONAL_TYPE_MAP.get(dataType);
//    }
}