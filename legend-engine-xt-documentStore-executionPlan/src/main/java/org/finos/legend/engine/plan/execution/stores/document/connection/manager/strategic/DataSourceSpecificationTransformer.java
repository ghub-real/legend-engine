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


import org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.AuthenticationStrategy;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecification;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecificationKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.specification.MongoDBDataSourceSpecification;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.specification.keys.MongoDBDataSourceSpecificationKey;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DataStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecificationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification;


public class
DataSourceSpecificationTransformer implements DatasourceSpecificationVisitor<DataSourceSpecification>
{

    private final DataSourceSpecificationKey key;
    private final DataStoreConnection connection;
    private final AuthenticationStrategy authenticationStrategy;

    public DataSourceSpecificationTransformer(DataSourceSpecificationKey key, AuthenticationStrategy authenticationStrategy, DataStoreConnection connection)
    {
        this.key = key;
        this.authenticationStrategy = authenticationStrategy;
        this.connection = connection;
    }


    @Override
    public DataSourceSpecification visit(DatasourceSpecification datasourceSpecification)
    {
        if (datasourceSpecification instanceof MongoDBDatasourceSpecification)
        {
            return new MongoDBDataSourceSpecification(
                    (MongoDBDataSourceSpecificationKey) key,
                    //new RedshiftManager(),
                    authenticationStrategy
            );
        }

        return null;
    }
}
