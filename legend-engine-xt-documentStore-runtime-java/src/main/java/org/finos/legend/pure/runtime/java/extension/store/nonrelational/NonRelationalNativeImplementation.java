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

package org.finos.legend.pure.runtime.java.extension.store.nonrelational;

import org.eclipse.collections.api.block.function.Function0;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentNull;
import org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_execute_Document;
import org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_execute_ResultSet;
import org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_runtime_DataSource;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_DocumentStoreConnection;
import org.finos.legend.pure.m3.execution.ExecutionSupport;
import org.finos.legend.pure.m4.coreinstance.SourceInformation;
import org.finos.legend.pure.runtime.java.compiled.execution.CompiledExecutionSupport;
import org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared.ClientWithDataSourceInfo;
import org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared.IClientManagerHandler;

public class NonRelationalNativeImplementation
{
    private static final IClientManagerHandler clientManagerHandler = IClientManagerHandler.CLIENT_MANAGER_HANDLER;

    public static Root_meta_external_store_document_metamodel_execute_ResultSet executeInDb(String sql, Root_meta_external_store_document_runtime_connections_DocumentStoreConnection pureConnection, long queryTimeoutInSeconds, long fetchSize, SourceInformation si, Function0<Root_meta_external_store_document_metamodel_execute_ResultSet> resultSetBuilder, Function0<Root_meta_external_store_document_metamodel_DocumentNull> docNullBuilder, Function0<Root_meta_external_store_document_metamodel_execute_Document> rowBuilder, Function0<Root_meta_external_store_document_metamodel_runtime_DataSource> datasourceBuilder, ExecutionSupport es)
    {
        Root_meta_external_store_document_metamodel_execute_ResultSet documentResult = resultSetBuilder.value();

        //Connection connection = null;
        NonRelationalClient client = null; // new LocalMongoDbClient();

        ClientWithDataSourceInfo clientWithDataSourceInfo = null;


        long startRequestConnection = System.nanoTime();
        clientWithDataSourceInfo = clientManagerHandler.getClientWithDataSourceInfo(pureConnection, ((CompiledExecutionSupport) es).getProcessorSupport());
        client = clientWithDataSourceInfo.getNonRelationalClient();

        documentResult._connectionAcquisitionTimeInNanoSecond(System.nanoTime() - startRequestConnection);

        Root_meta_external_store_document_metamodel_DocumentNull docNull = docNullBuilder.value();
        String tz = pureConnection._timeZone() == null ? "GMT" : pureConnection._timeZone();

        String URL = clientManagerHandler.getPotentialDebug(pureConnection, client);
        if (URL != null)
        {
            documentResult = documentResult._executionPlanInformation(URL);
        }

//        ResultSetRowIterableProvider.ResultSetIterableContainer resultContainer = ResultSetRowIterableProvider.createResultSetIterator(pureConnection, connection, sql, RelationalExecutionProperties.getMaxRows(), RelationalExecutionProperties.shouldThrowIfMaxRowsExceeded(), (int) queryTimeoutInSeconds, (int) fetchSize, new RelationalNativeImplementation.CreateRowFunction(pureResult, rowBuilder), sqlNull, tz, si, (CompiledExecutionSupport) es, connectionWithDataSourceInfo);
//        pureResult._columnNamesAddAll(resultContainer.columnNames);
//        pureResult._executionTimeInNanoSecond(resultContainer.queryTimeInNanos);
//        pureResult._rows(resultContainer.rowIterable);
//
//        String dbHost = connectionWithDataSourceInfo.getDataSource().getHost();
//        Integer dbPort = connectionWithDataSourceInfo.getDataSource().getPort();
//        String dbName = connectionWithDataSourceInfo.getDataSource().getDataSourceName();
//        String serverPrincipal = connectionWithDataSourceInfo.getDataSource().getServerPrincipal();
//        if (pureConnection._type() != null && dbHost != null && dbPort != null && dbName != null)
//        {
//            DataSource ds = datasourceBuilder.value();//new Root_meta_relational_runtime_DataSource_Impl("ID");
//            ds._type(pureConnection._type());
//            ds._port(dbPort.longValue());
//            ds._host(dbHost);
//            ds._name(dbName);
//            if (serverPrincipal != null) ds._serverPrincipal(serverPrincipal);
//            pureResult._dataSource(ds);
//        }
        return documentResult;

    }
}
