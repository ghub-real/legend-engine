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

package org.finos.legend.engine.plan.execution.stores.document.connection.manager.strategic;

import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecificationKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.specification.keys.MongoDBDataSourceSpecificationKey;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DataStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecificationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification;


public class DataSourceSpecificationKeyGenerator implements DatasourceSpecificationVisitor<DataSourceSpecificationKey>
{
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String TEST_DB = "testDB";
    private final int testDbPort;
    private final DataStoreConnection connection;

    public DataSourceSpecificationKeyGenerator(int testDbPort, DataStoreConnection connection)
    {
        this.testDbPort = testDbPort;
        this.connection = connection;
    }

    @Override
    public DataSourceSpecificationKey visit(DatasourceSpecification datasourceSpecification)
    {
        if (datasourceSpecification instanceof MongoDBDatasourceSpecification)
        {
            MongoDBDatasourceSpecification mongoDBDataSourceSpecification = (MongoDBDatasourceSpecification) datasourceSpecification;
            return new MongoDBDataSourceSpecificationKey(
                    mongoDBDataSourceSpecification.host,
                    mongoDBDataSourceSpecification.port,
                    mongoDBDataSourceSpecification.databaseName
            );
        }
        return null;
    }
}
