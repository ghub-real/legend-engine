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

package org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds;

import org.finos.legend.engine.authentication.credential.CredentialSupplier;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.AuthenticationStrategy;
import org.finos.legend.engine.plan.execution.stores.nonrelational.LocalMongoDbClient;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ConnectionKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.state.IdentityState;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.finos.legend.engine.shared.core.identity.credential.LegendKerberosCredential;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

public abstract class DataSourceSpecification
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DataSourceSpecification.class);
    protected final DataSourceSpecificationKey datasourceKey;
    private final AuthenticationStrategy authenticationStrategy;
    private final ConnectionKey connectionKey;

    protected DataSourceSpecification(DataSourceSpecificationKey key, AuthenticationStrategy authenticationStrategy)  //DatabaseManager databaseManager,
    {
        this.datasourceKey = key;
        this.authenticationStrategy = authenticationStrategy;
        this.connectionKey = new ConnectionKey(this.datasourceKey, this.authenticationStrategy.getKey());
        LOGGER.info("Created new {}", this);
    }

    public NonRelationalClient getClientUsingIdentity(Identity identity, Optional<CredentialSupplier> databaseCredentialSupplierHolder)
    {
        Optional<LegendKerberosCredential> kerberosCredentialHolder = identity.getCredential(LegendKerberosCredential.class);
        Supplier<NonRelationalClient> dataSourceBuilder;
        dataSourceBuilder = () -> this.buildNonRelationalClient(identity);

        return getNonRelationalClient(new IdentityState(identity, databaseCredentialSupplierHolder), dataSourceBuilder);
    }

    private NonRelationalClient getNonRelationalClient(IdentityState identityState, Supplier<NonRelationalClient> dataSourceBuilder)
    {
        // TODO: Integrate identity state.
        return dataSourceBuilder.get();
    }

    private NonRelationalClient buildNonRelationalClient(Identity identity)
    {
        // TODO: Move this to connection manager to abstract this bit of reference to implementation.
        // TODO: Integrate Identity in connection
        return new LocalMongoDbClient(getConnectionURI(null, -1, null, new Properties()));
    }


    protected String getConnectionURI(String host, int port, String databaseName, Properties properties)
    {
        //Use database manager to create default URL via buildURl()
        return host + ":" + port;
    }
}
