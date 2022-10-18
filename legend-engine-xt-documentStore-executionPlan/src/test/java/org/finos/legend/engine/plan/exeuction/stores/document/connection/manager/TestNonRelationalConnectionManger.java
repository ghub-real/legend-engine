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

package org.finos.legend.engine.plan.exeuction.stores.document.connection.manager;

import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.engine.plan.execution.stores.document.connection.manager.strategic.NonRelationalConnectionManager;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DataStoreConnection;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.finos.legend.engine.shared.core.identity.factory.DefaultIdentityFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class TestNonRelationalConnectionManger
{
    @Test
    public void testConnection() throws Exception
    {
        NonRelationalConnectionManager manager = new NonRelationalConnectionManager(22, Lists.mutable.empty());
        String connectionStr = "{\n" +
                "  \"_type\": \"DataStoreConnection\",\n" +
                "  \"type\": \"Mongo\",\n" +
                "  \"authenticationStrategy\" : {\n" +
                "    \"_type\" : \"userNamePassword\",\n" +
                "    \"baseVaultReference\" : \"basePath\",\n" +
                "    \"userNameVaultReference\" : \"userRef\",\n" +
                "    \"passwordVaultReference\" : \"passRef\"\n" +
                "  },\n" +
                "  \"datasourceSpecification\" : {\n" +
                "    \"_type\" : \"mongoDB\",\n" +
                "    \"host\" : \"localhost\",\n" +
                "    \"port\" : \"27017\",\n" +
                "    \"databaseName\" : \"defaultdb\"\n" +
                "  }\n" +
                "}";

        DataStoreConnection connectionSpec = ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports().readValue(connectionStr, DataStoreConnection.class);
        Identity identity = DefaultIdentityFactory.INSTANCE.makeUnknownIdentity();
        NonRelationalClient client = manager.getDataSourceSpecification(connectionSpec).getClientUsingIdentity(identity, Optional.empty());
        Assert.assertNotNull("NonRelationalClient should not to null", client);
        String defaultClusterDesc = "ClusterDescription{type=UNKNOWN, connectionMode=SINGLE, serverDescriptions=[ServerDescription{address=localhost:27017, type=UNKNOWN, state=CONNECTING}]}";
        Assert.assertEquals(defaultClusterDesc, client.getMongoDBClient().getClusterDescription().toString());
    }
}

/*

 */