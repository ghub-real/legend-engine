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

package org.finos.legend.pure.runtime.java.extension.store.nonrelational.interpreted.natives;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.finos.legend.pure.m3.compiler.Context;
import org.finos.legend.pure.m3.exception.PureExecutionException;
import org.finos.legend.pure.m3.navigation.Instance;
import org.finos.legend.pure.m3.navigation.M3Properties;
import org.finos.legend.pure.m3.navigation.PrimitiveUtilities;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m3.navigation.ValueSpecificationBootstrap;
import org.finos.legend.pure.m3.serialization.runtime.Message;
import org.finos.legend.pure.m4.ModelRepository;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;
import org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared.IClientManagerHandler;
import org.finos.legend.pure.runtime.java.interpreted.ExecutionSupport;
import org.finos.legend.pure.runtime.java.interpreted.VariableContext;
import org.finos.legend.pure.runtime.java.interpreted.natives.core.InstantiationContext;
import org.finos.legend.pure.runtime.java.interpreted.natives.core.NativeFunction;
import org.finos.legend.pure.runtime.java.interpreted.profiler.Profiler;

import java.util.Stack;

public class ExecuteInDb extends NativeFunction
{
    private static final IClientManagerHandler clientManagerHandler = IClientManagerHandler.CLIENT_MANAGER_HANDLER;

    private final ModelRepository repository;
    private final Message message;
    private int maxRows;

    public ExecuteInDb(ModelRepository repository, Message message, int maxRows)
    {
        this.repository = repository;
        this.message = message;
        this.maxRows = (maxRows < 0) ? 0 : maxRows;
    }

    public CoreInstance executeInDb(CoreInstance connectionInformation, String sql, int timeOutInSeconds, int fetchSize, CoreInstance functionExpressionToUseInStack, ProcessorSupport processorSupport)
    {
        CoreInstance resultSetClassifier = processorSupport.package_getByUserPath("meta::relational::metamodel::execute::ResultSet");
        if (resultSetClassifier == null)
        {
            throw new RuntimeException("'meta::relational::metamodel::execute::ResultSet' is unknown");
        }
        CoreInstance rowClassifier = processorSupport.package_getByUserPath("meta::relational::metamodel::execute::Row");
        if (rowClassifier == null)
        {
            throw new RuntimeException("'meta::relational::metamodel::execute::Row' is unknown");
        }

        CoreInstance pureResult = this.repository.newAnonymousCoreInstance(functionExpressionToUseInStack.getSourceInformation(), resultSetClassifier);
//
//        Connection connection = null;
//        ConnectionWithDataSourceInfo connectionWithDataSourceInfo = null;
//        Statement statement = null;
//        try
//        {
//            try
//            {
//                MetricsRecorder.incrementRelationalExecutionCounters();
//                this.message.setMessage("Acquiring connection...");
//
//                CoreInstance dbTimeZone = connectionInformation.getValueForMetaPropertyToOne("timeZone");
//                String tz = dbTimeZone == null ? "GMT" : dbTimeZone.getName();
//
//                long startRequestConnection = System.nanoTime();
//                connectionWithDataSourceInfo = clientManagerHandler.getConnectionWithDataSourceInfo(connectionInformation, processorSupport);
//                Instance.addValueToProperty(pureResult, "connectionAcquisitionTimeInNanoSecond", this.repository.newIntegerCoreInstance(System.nanoTime() - startRequestConnection), processorSupport);
//
//                connection = connectionWithDataSourceInfo.getConnection();
//                if (!PureConnectionUtils.isPureConnectionType(connectionInformation, "Hive"))
//                {
//                    connection.setAutoCommit(true);
//
//                }
//
//                statement = connection.createStatement();
//                int actualFetchSize = this.maxRows > 0 ? Math.min(fetchSize, this.maxRows) : fetchSize;
//                clientManagerHandler.registerStatement(statement, sql, actualFetchSize, timeOutInSeconds);
//                statement.setMaxRows(this.maxRows);
//                statement.setFetchSize(actualFetchSize);
//                if (!PureConnectionUtils.isPureConnectionType(connectionInformation, "Hive"))
//                {
//                    statement.setQueryTimeout(timeOutInSeconds);
//
//                }
//
//
//                clientManagerHandler.addPotentialDebug(connectionInformation, statement);
//                this.message.setMessage("Executing SQL...");
//                long start = System.nanoTime();
//                if (statement.execute(sql))
//                {
//                    String URL = clientManagerHandler.getPotentialDebug(connectionInformation, connection);
//                    if (URL != null)
//                    {
//                        Instance.addValueToProperty(pureResult, "executionPlanInformation", this.repository.newStringCoreInstance(URL), processorSupport);
//                    }
//
//                    ResultSet rs = statement.getResultSet();
//
//                    createPureResultSetFromDatabaseResultSet(pureResult, rs, functionExpressionToUseInStack, rowClassifier, tz, repository, start, this.maxRows, processorSupport);
//                }
//                else
//                {
//                    Instance.addValueToProperty(pureResult, "executionTimeInNanoSecond", this.repository.newIntegerCoreInstance(0), processorSupport);
//                }
//
//                CoreInstance dbType = Instance.getValueForMetaPropertyToOneResolved(connectionInformation, "type", processorSupport);
//                String dbHost = connectionWithDataSourceInfo.getDataSource().getHost();
//                Integer dbPort = connectionWithDataSourceInfo.getDataSource().getPort();
//                String dbName = connectionWithDataSourceInfo.getDataSource().getDataSourceName();
//                String serverPrincipal = connectionWithDataSourceInfo.getDataSource().getServerPrincipal();
//
//                if (dbType != null && dbHost != null && dbName != null && dbPort != null)
//                {
//                    CoreInstance dataSourceCoreInstance = this.repository.newEphemeralAnonymousCoreInstance(null, processorSupport.package_getByUserPath("meta::relational::runtime::DataSource"));
//
//                    Instance.addValueToProperty(dataSourceCoreInstance, "host", this.repository.newStringCoreInstance(dbHost), processorSupport);
//                    Instance.addValueToProperty(dataSourceCoreInstance, "port", this.repository.newIntegerCoreInstance(dbPort), processorSupport);
//                    Instance.addValueToProperty(dataSourceCoreInstance, "name", this.repository.newStringCoreInstance(dbName), processorSupport);
//                    Instance.addValueToProperty(dataSourceCoreInstance, "type", dbType, processorSupport);
//                    if (serverPrincipal != null)
//                        Instance.addValueToProperty(dataSourceCoreInstance, "serverPrincipal", this.repository.newStringCoreInstance(serverPrincipal), processorSupport);
//                    Instance.addValueToProperty(pureResult, "dataSource", dataSourceCoreInstance, processorSupport);
//                }
//            }
//            finally
//            {
//                if (statement != null)
//                {
//                    clientManagerHandler.unregisterStatement(statement);
//                    statement.close();
//                }
//                if (connection != null)
//                {
//                    connection.close();
//                }
//                MetricsRecorder.decrementCurrentRelationalExecutionCounter();
//            }
//        }
//        catch (SQLException e)
//        {
//            throw new PureExecutionException(functionExpressionToUseInStack.getSourceInformation(), SQLExceptionHandler.buildExceptionString(e, connection), e);
//        }

        this.message.setMessage("Executing SQL...[DONE]");
        return pureResult;
    }

    @Override
    public CoreInstance execute(ListIterable<? extends CoreInstance> params, Stack<MutableMap<String, CoreInstance>> stack, Stack<MutableMap<String, CoreInstance>> stack1, VariableContext variableContext, CoreInstance functionExpressionToUseInStack, Profiler profiler, InstantiationContext instantiationContext, ExecutionSupport executionSupport, Context context, ProcessorSupport processorSupport) throws PureExecutionException
    {
        String sql = Instance.getValueForMetaPropertyToOneResolved(params.get(0), M3Properties.values, processorSupport).getName();
        CoreInstance connectionInformation = Instance.getValueForMetaPropertyToOneResolved(params.get(1), M3Properties.values, processorSupport);

        Number timeOutInSeconds = PrimitiveUtilities.getIntegerValue(Instance.getValueForMetaPropertyToOneResolved(params.get(2), M3Properties.values, processorSupport));
        Number fetchSize = PrimitiveUtilities.getIntegerValue(Instance.getValueForMetaPropertyToOneResolved(params.get(3), M3Properties.values, processorSupport));

        CoreInstance pureResult = this.executeInDb(connectionInformation, sql, timeOutInSeconds.intValue(), fetchSize.intValue(), functionExpressionToUseInStack, processorSupport);
        return ValueSpecificationBootstrap.wrapValueSpecification(pureResult, true, processorSupport);
    }
}
