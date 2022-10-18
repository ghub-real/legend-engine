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

package org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.specification;

import org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.AuthenticationStrategy;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecification;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecificationKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.specification.keys.MongoDBDataSourceSpecificationKey;

import java.util.Properties;

public class MongoDBDataSourceSpecification extends DataSourceSpecification
{
    public MongoDBDataSourceSpecification(DataSourceSpecificationKey key, AuthenticationStrategy authenticationStrategy)
    {
        super(key, authenticationStrategy);
    }

    protected String getConnectionURI(String host, int port, String databaseName, Properties properties)
    {
        //Use database manager to create default URL via buildURl()
        String configHost = ((MongoDBDataSourceSpecificationKey) this.datasourceKey).getHost();
        int configPort = ((MongoDBDataSourceSpecificationKey) this.datasourceKey).getPort();
        String uri = "mongodb://" + configHost + ":" + configPort;

        return uri;
    }
}

