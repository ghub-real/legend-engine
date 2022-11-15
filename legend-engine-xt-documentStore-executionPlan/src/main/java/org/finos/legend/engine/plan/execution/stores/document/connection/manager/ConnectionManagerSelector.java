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

package org.finos.legend.engine.plan.execution.stores.document.connection.manager;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.utility.Iterate;
import org.finos.legend.engine.authentication.credential.CredentialSupplier;
import org.finos.legend.engine.authentication.provider.DatabaseAuthenticationFlowProvider;
import org.finos.legend.engine.plan.execution.stores.StoreExecutionState;
import org.finos.legend.engine.plan.execution.stores.document.config.TemporaryTestDbConfiguration;
import org.finos.legend.engine.plan.execution.stores.document.connection.manager.strategic.NonRelationalConnectionManager;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ConnectionKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecification;
import org.finos.legend.engine.plan.execution.stores.relational.connection.authentication.strategy.OAuthProfile;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DataStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.TestDatabaseAuthenticationStrategy;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.finos.legend.engine.shared.core.identity.factory.IdentityFactoryProvider;
import org.pac4j.core.profile.CommonProfile;

import javax.security.auth.Subject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

public class ConnectionManagerSelector
{
    private final Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder;
    private MutableList<org.finos.legend.engine.plan.execution.stores.document.connection.manager.ConnectionManager> connectionManagers;

    public ConnectionManagerSelector(TemporaryTestDbConfiguration temporaryTestDb, List<OAuthProfile> oauthProfiles)
    {
        this(temporaryTestDb, oauthProfiles, Optional.empty());
    }

    public ConnectionManagerSelector(TemporaryTestDbConfiguration temporaryTestDb, List<OAuthProfile> oauthProfiles, Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder)
    {
        MutableList<org.finos.legend.engine.plan.execution.stores.document.connection.manager.ConnectionManagerExtension> extensions = Iterate.addAllTo(ServiceLoader.load(ConnectionManagerExtension.class), Lists.mutable.empty());
        this.connectionManagers = Lists.mutable.<org.finos.legend.engine.plan.execution.stores.document.connection.manager.ConnectionManager>with(
                new NonRelationalConnectionManager(temporaryTestDb.port, oauthProfiles, flowProviderHolder)
        ).withAll(extensions.collect(e -> e.getExtensionManager(temporaryTestDb.port, oauthProfiles)));
        this.flowProviderHolder = flowProviderHolder;
    }

    public static org.finos.legend.engine.protocol.pure.v1.model.packageableElement.connection.Connection transformToTestConnectionSpecification(org.finos.legend.engine.protocol.pure.v1.model.packageableElement.connection.Connection originalConnection, String testData, List<String> setupSqls)
    {
        DataStoreConnection db = new DataStoreConnection();
        db.datasourceSpecification = new org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification(); //testData, setupSqls
        db.authenticationStrategy = new TestDatabaseAuthenticationStrategy();
        db.databaseType = DatabaseType.Mongo;
        db.type = DatabaseType.Mongo;
        db.element = originalConnection.element;
        db.timeZone = originalConnection instanceof DatabaseConnection ? ((DatabaseConnection) originalConnection).timeZone : null;
        db.quoteIdentifiers = originalConnection instanceof DatabaseConnection ? ((DatabaseConnection) originalConnection).quoteIdentifiers : null;
        return db;
    }

    public NonRelationalClient getDatabaseConnection(MutableList<CommonProfile> profiles, DatabaseConnection databaseConnection)
    {
        return this.getDatabaseConnection(profiles, databaseConnection, StoreExecutionState.emptyRuntimeContext());
    }

    public NonRelationalClient getDatabaseConnection(MutableList<CommonProfile> profiles, DatabaseConnection databaseConnection, StoreExecutionState.RuntimeContext runtimeContext)
    {
        DataSourceSpecification datasource = getDataSourceSpecification(databaseConnection);
        Identity identity = IdentityFactoryProvider.getInstance().makeIdentity(profiles);
        return this.getDatabaseConnectionImpl(identity, databaseConnection, datasource, runtimeContext);
    }

    public NonRelationalClient getDatabaseConnection(Subject subject, DatabaseConnection databaseConnection)
    {
        return this.getDatabaseConnection(subject, databaseConnection, StoreExecutionState.emptyRuntimeContext());
    }

    public NonRelationalClient getDatabaseConnection(Subject subject, DatabaseConnection databaseConnection, StoreExecutionState.RuntimeContext runtimeContext)
    {
        DataSourceSpecification datasource = getDataSourceSpecification(databaseConnection);
        Identity identity = IdentityFactoryProvider.getInstance().makeIdentity(subject);
        return this.getDatabaseConnectionImpl(identity, databaseConnection, datasource, runtimeContext);
    }

    private DataSourceSpecification getDataSourceSpecification(DatabaseConnection databaseConnection)
    {
        DataSourceSpecification datasource = this.connectionManagers.collect(c -> c.getDataSourceSpecification(databaseConnection)).detect(Objects::nonNull);
        if (datasource == null)
        {
            throw new RuntimeException("Not Supported! " + databaseConnection.getClass());
        }
        return datasource;
    }
/*
    public Connection getDatabaseConnection(Identity identity, DatabaseConnection databaseConnection)
    {
        return this.getDatabaseConnection(identity, databaseConnection, StoreExecutionState.emptyRuntimeContext());
    }

    public Connection getDatabaseConnection(Identity identity, DatabaseConnection databaseConnection, StoreExecutionState.RuntimeContext runtimeContext)
    {
        DataSourceSpecification datasource = getDataSourceSpecification(databaseConnection);
        return this.getDatabaseConnectionImpl(identity, databaseConnection, datasource, runtimeContext);
    }*/

    public NonRelationalClient getDatabaseConnectionImpl(Identity identity, DatabaseConnection databaseConnection, DataSourceSpecification datasource, StoreExecutionState.RuntimeContext runtimeContext)
    {
        if (databaseConnection instanceof DataStoreConnection)
        {
            DataStoreConnection nonRelationalDatabaseConnection = (DataStoreConnection) databaseConnection;
            Optional<CredentialSupplier> databaseCredentialHolder = NonRelationalConnectionManager.getCredential(flowProviderHolder, nonRelationalDatabaseConnection, identity, runtimeContext);
            return datasource.getClientUsingIdentity(identity, databaseCredentialHolder);
        }
        /*
            In some cases, connection managers can return DatabaseConnections that are not RelationalDatabaseConnection.
            Without the metadata associated with a RelationalDatabaseConnection we cannot compute a credential.
        */
        return datasource.getClientUsingIdentity(identity, Optional.empty());
    }

    public ConnectionKey generateKeyFromDatabaseConnection(DatabaseConnection databaseConnection)
    {
        ConnectionKey key = this.connectionManagers.collect(c -> c.generateKeyFromDatabaseConnection(databaseConnection)).detect(Objects::nonNull);
        if (key == null)
        {
            throw new RuntimeException("Not Supported! " + databaseConnection.getClass());
        }
        return key;
    }

}