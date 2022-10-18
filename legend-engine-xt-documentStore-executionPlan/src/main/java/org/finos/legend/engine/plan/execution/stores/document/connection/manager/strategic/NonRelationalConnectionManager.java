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

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.authentication.credential.CredentialSupplier;
import org.finos.legend.engine.authentication.provider.DatabaseAuthenticationFlowProvider;
import org.finos.legend.engine.plan.execution.stores.StoreExecutionState;
import org.finos.legend.engine.plan.execution.stores.document.connection.manager.ConnectionManager;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ConnectionKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.strategy.UserNamePasswordAuthenticationStrategy;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.strategy.keys.AuthenticationStrategyKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecification;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecificationKey;
import org.finos.legend.engine.plan.execution.stores.relational.connection.authentication.strategy.OAuthProfile;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DataStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategyVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.TestDatabaseAuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecificationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.finos.legend.engine.shared.core.identity.factory.IdentityFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class NonRelationalConnectionManager implements ConnectionManager
{
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String TEST_DB = "default";
    private static final Logger LOGGER = LoggerFactory.getLogger(NonRelationalConnectionManager.class);
    private int testDbPort = 27017;
    private MutableList<AuthenticationStrategyVisitor<AuthenticationStrategyKey>> authenticationStrategyKeyVisitors;

    // private MutableList<AuthenticationStrategyVisitor<org.finos.legend.engine.plan.execution.stores.relational.connection.authentication.AuthenticationStrategy>> authenticationStrategyTrans;
    private MutableList<Function<DataStoreConnection, DatasourceSpecificationVisitor<DataSourceSpecificationKey>>> dataSourceKeys;
    private MutableList<Function2<DataStoreConnection, ConnectionKey, DatasourceSpecificationVisitor<DataSourceSpecification>>> dataSourceTrans;
    private Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder;

    public NonRelationalConnectionManager(int testDbPort, List<OAuthProfile> oauthProfiles)
    {
        this(testDbPort, oauthProfiles, Optional.empty());
    }

    public NonRelationalConnectionManager(int testDbPort, List<OAuthProfile> oauthProfiles, Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder)
    {
        this.flowProviderHolder = flowProviderHolder;
        //MutableList<StrategicConnectionExtension> extensions = Iterate.addAllTo(ServiceLoader.load(StrategicConnectionExtension.class), Lists.mutable.empty());

        this.testDbPort = testDbPort;
        this.authenticationStrategyKeyVisitors =
                Lists.mutable.<AuthenticationStrategyVisitor<AuthenticationStrategyKey>>with(new AuthenticationStrategyKeyGenerator());
                        // .withAll(extensions.collect(StrategicConnectionExtension::getExtraAuthenticationKeyGenerators));

//        this.authenticationStrategyTrans =
//                Lists.mutable.<AuthenticationStrategyVisitor<org.finos.legend.engine.plan.execution.stores.relational.connection.authentication.AuthenticationStrategy>>with(new AuthenticationStrategyTransformer(oauthProfiles))
//                        .withAll(extensions.collect(extension -> extension.getExtraAuthenticationStrategyTransformGenerators(oauthProfiles)));

//        Function<RelationalDatabaseConnection, org.finos.legend.engine.plan.execution.stores.relational.connection.authentication.AuthenticationStrategy> authenticationStrategyProvider =
//                r -> ListIterate.collect(this.authenticationStrategyTrans, visitor -> r.authenticationStrategy.accept(visitor))
//                        .select(Objects::nonNull)
//                        .getFirstOptional()
//                        .orElseThrow(() -> new UnsupportedOperationException("No transformer provided for Authentication Strategy - " + r.authenticationStrategy.getClass().getName()));


        Function<DataStoreConnection, org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.AuthenticationStrategy> authenticationStrategyProvider = (dsConnection) -> new UserNamePasswordAuthenticationStrategy("user", "pass");
        this.dataSourceKeys =
                Lists.mutable.<Function<DataStoreConnection, org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecificationVisitor<org.finos.legend.engine.plan.execution.stores.nonrelational.client.ds.DataSourceSpecificationKey>>>with(c -> new DataSourceSpecificationKeyGenerator(testDbPort, c));
        //.withAll(extensions.collect(a -> a.getExtraDataSourceSpecificationKeyGenerators(testDbPort)));

        this.dataSourceTrans =
                Lists.mutable.<Function2<DataStoreConnection, ConnectionKey, DatasourceSpecificationVisitor<DataSourceSpecification>>>with(
                        (r, c) -> new DataSourceSpecificationTransformer(
                                c.getDataSourceSpecificationKey(),
                                authenticationStrategyProvider.apply(r),
                                r));
        //.withAll(extensions.collect(a -> a.getExtraDataSourceSpecificationTransformerGenerators(authenticationStrategyProvider)));
    }

    public static Optional<CredentialSupplier> getCredential(Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder, DataStoreConnection connection, Identity identity, StoreExecutionState.RuntimeContext runtimeContext)
    {
        if (!flowProviderHolder.isPresent())
        {
            // The use of flows has not been enabled
            return Optional.empty();
        }
        return NonRelationalConnectionManager.getCredential(flowProviderHolder.get(), connection, identity, runtimeContext);
    }

    public static Optional<CredentialSupplier> getCredential(DatabaseAuthenticationFlowProvider flowProvider, DataStoreConnection connection, Identity identity, StoreExecutionState.RuntimeContext runtimeContext)
    {
        return Optional.empty();
//        Optional<DatabaseAuthenticationFlow> flowHolder = flowProvider.lookupFlow(connection);
//        if (!flowHolder.isPresent())
//        {
//            /*
//              When the flow feature is fully enabled, a missing flow is a bug and should be failed at runtime.
//              Fow now, we are lenient and fallback to the existing implementation which uses identity directly.
//            */
//            String message = String.format(
//                    "Database authentication flow feature has been enabled. But flow for DbType=%s, AuthType=%s has not been configured",
//                    connection.datasourceSpecification.getClass().getSimpleName(),
//                    connection.authenticationStrategy.getClass().getSimpleName());
//            LOGGER.warn(message);
//            return Optional.empty();
//        }
//        CredentialSupplier credentialSupplier = new CredentialSupplier(flowHolder.get(), connection.datasourceSpecification, connection.authenticationStrategy, DatabaseAuthenticationFlow.RuntimeContext.newWith(runtimeContext.getContextParams()));
//        return Optional.of(credentialSupplier);
    }

    private DataSourceSpecificationKey buildDataSourceKey(DatasourceSpecification datasourceSpecification, DataStoreConnection nonRelationalDatabaseConnection)
    {
        // TODO: goncah Fix this, as we are not building dataSourceKeys corrently on startup for the NonRelationalManager
        DataSourceSpecificationKey key = this.dataSourceKeys.collect(f -> datasourceSpecification.accept(f.apply(nonRelationalDatabaseConnection))).detect(Objects::nonNull);
        if (key == null)
        {
            throw new RuntimeException(datasourceSpecification.getClass() + " is not supported!");
        }
        return key;
    }

    private AuthenticationStrategyKey buildAuthStrategyKey(AuthenticationStrategy authenticationStrategy)
    {
        AuthenticationStrategyKey key = this.authenticationStrategyKeyVisitors.collect(f -> authenticationStrategy.accept(f)).detect(Objects::nonNull);
        //AuthenticationStrategyKey key = null;
        if (key == null)
        {
            throw new RuntimeException(authenticationStrategy.getClass() + " is not supported!");
        }
        return key;
    }

    private DataSourceSpecification buildDataSourceTrans(DatasourceSpecification datasourceSpecification, DataStoreConnection nonRelationalDatabaseConnection, ConnectionKey connectionKey)
    {
        DataSourceSpecification key = this.dataSourceTrans.collect(f -> datasourceSpecification.accept(f.value(nonRelationalDatabaseConnection, connectionKey))).detect(Objects::nonNull);
        if (key == null)
        {
            throw new RuntimeException(datasourceSpecification.getClass() + " is not supported!");
        }
        return key;
    }

    public NonRelationalClient getTestDatabaseConnection()
    {
        // TODO : pass identity into this method
        DataStoreConnection testConnection = buildTestDatabaseDatasourceSpecification();
        Identity identity = IdentityFactoryProvider.getInstance().makeIdentity((Subject) null);
        Optional<CredentialSupplier> credentialHolder = NonRelationalConnectionManager.getCredential(flowProviderHolder, testConnection, identity, StoreExecutionState.emptyRuntimeContext());
        return this.getDataSourceSpecification(testConnection).getClientUsingIdentity(identity, credentialHolder);
    }

    public DataStoreConnection buildTestDatabaseDatasourceSpecification()
    {
        MongoDBDatasourceSpecification datasourceSpecification = new MongoDBDatasourceSpecification();
        datasourceSpecification.databaseName = TEST_DB;
        datasourceSpecification.host = LOCAL_HOST;
        datasourceSpecification.port = testDbPort;

        DataStoreConnection testConnection = new DataStoreConnection();
        testConnection.datasourceSpecification = datasourceSpecification;
        testConnection.authenticationStrategy = new TestDatabaseAuthenticationStrategy();
        testConnection.type = DatabaseType.Mongo;
        return testConnection;
    }

    public ConnectionKey generateKeyFromDatabaseConnection(DatabaseConnection connection)
    {
        if (connection instanceof DataStoreConnection)
        {
            DataStoreConnection nonRelationalDatabaseConnection = (DataStoreConnection) connection;
            return new ConnectionKey(
                    buildDataSourceKey(nonRelationalDatabaseConnection.datasourceSpecification, nonRelationalDatabaseConnection),
                    buildAuthStrategyKey(nonRelationalDatabaseConnection.authenticationStrategy)
            );
        }
        return null;
    }

    public DataSourceSpecification getDataSourceSpecification(DatabaseConnection connection)
    {
        if (connection instanceof DataStoreConnection)
        {
            DataStoreConnection nonRelationalDatabaseConnection = (DataStoreConnection) connection;
            ConnectionKey connectionKey = this.generateKeyFromDatabaseConnection(connection);
            return buildDataSourceTrans(nonRelationalDatabaseConnection.datasourceSpecification, nonRelationalDatabaseConnection, connectionKey);
        }
        return null;
    }

}
