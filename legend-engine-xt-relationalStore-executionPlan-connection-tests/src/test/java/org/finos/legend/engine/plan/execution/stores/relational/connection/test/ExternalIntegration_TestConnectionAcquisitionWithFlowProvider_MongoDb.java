// Copyright 2021 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.plan.execution.stores.relational.connection.test;

import com.mongodb.MongoSecurityException;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.engine.authentication.DatabaseAuthenticationFlow;
import org.finos.legend.engine.authentication.LegendDefaultDatabaseAuthenticationFlowProvider;
import org.finos.legend.engine.authentication.LegendDefaultDatabaseAuthenticationFlowProviderConfiguration;
import org.finos.legend.engine.plan.execution.stores.relational.connection.test.nonrelational.client.MongoDbClient;
import org.finos.legend.engine.plan.execution.stores.relational.connection.test.nonrelational.client.NonRelationalClient;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.DatabaseType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.RelationalDatabaseConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.authentication.UserNamePasswordAuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.specification.StaticDatasourceSpecification;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class ExternalIntegration_TestConnectionAcquisitionWithFlowProvider_MongoDb
{


    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalIntegration_TestConnectionAcquisitionWithFlowProvider_MongoDb.class);
    public GenericContainer<MongoDBContainer> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:4.4.1"));

    public static final String DB_ROOT_USERNAME = "sa";
    public static final String DB_ROOT_PASSWORD = "sa";
    public static final String DB_DATABASE = "db";

    public static final String DB_AUTH_SOURCE = "admin"; // authentication DB, typically "admin" with mongo
    // mongodb.com/docs/manual/reference/connection-string/#mongodb-urioption-urioption.authSource

    private static final int MONGO_PORT = 27017;

    private static final String DB_USER_DATABASE = "userDatabase";
    private static final String DB_ADMIN_DATABASE = "admin";
    private static final String DB_USER_DB_EMPLOYEE_COLLECTION = "employee";

    private static final String DB_TEST_USER_1_USERNAME = "testUser1";
    private static final String DB_TEST_USER_1_PASSWORD = "tu1";
    private static final String DB_TEST_USER_2_USERNAME = "testUser2";
    private static final String DB_TEST_USER_2_PASSWORD = "tu2";


    @Before
    public void setup() throws Exception
    {

        Assume.assumeTrue("A Docker client must be running for this integration test.", DockerClientFactory.instance().isDockerAvailable());
        startMongoDbContainer();

        LegendDefaultDatabaseAuthenticationFlowProvider flowProvider = new LegendDefaultDatabaseAuthenticationFlowProvider();
        flowProvider.configure(new LegendDefaultDatabaseAuthenticationFlowProviderConfiguration());
        assertStaticMongoFlowProviderIsAvailable(flowProvider);

        NonRelationalClient client = mongoClientForRootAdminWithStaticUserNamePassword();

        MongoDatabase userDatabase = client.getMongoDBClient().getDatabase(DB_USER_DATABASE);
        userDatabase.createCollection(DB_USER_DB_EMPLOYEE_COLLECTION);

        //https://www.mongodb.com/docs/manual/reference/command/createRole/
        String createRoleCommand = "\n" +
                " {\n" +
                "     createRole: \"employeeReadOnly\",\n" +
                "     privileges: [\n" +
                "       {\n" +
                "          resource: {\n" +
                "            role: 'read',\n" +
                "            db: '" + DB_USER_DATABASE + "',\n" +
                "            collection: '" + DB_USER_DB_EMPLOYEE_COLLECTION + "'\n" +
                "          }, actions: [\"find\"]\n" +
                "        }\n" +
                "     ],\n" +
                "     roles: [\n" +
                "     ]\n" +
                "   }";

        client.executeNativeCommandAsJson(DB_USER_DATABASE, createRoleCommand);

        //https://www.mongodb.com/docs/manual/reference/command/createUser/
        String createUserCommand = "\n" +
                "   {\n" +
                "     createUser: \"" + DB_TEST_USER_1_USERNAME + "\",\n" +
                "     pwd: \"" + DB_TEST_USER_1_PASSWORD + "\",     \n" +
                "     \n" +
                "     roles: [\n" +
                "      { role: 'employeeReadOnly', db: '" + DB_USER_DATABASE + "'}\n" +
                "     ],\n" +
                "    \n" +
                "   }\n";


        client.executeNativeCommandAsJson(DB_USER_DATABASE, createUserCommand);
        client.shutDown();
    }

    private void startMongoDbContainer()
    {
        try
        {

            List<String> list = new ArrayList<>();
            list.add("MONGO_INITDB_ROOT_USERNAME=" + DB_ROOT_USERNAME);
            list.add("MONGO_INITDB_ROOT_PASSWORD=" + DB_ROOT_PASSWORD);
            list.add("MONGO_INITDB_DATABASE=" + DB_DATABASE);
            mongoDBContainer.setEnv(list);
            mongoDBContainer.withExposedPorts(MONGO_PORT);
            mongoDBContainer.start();


            LOGGER.info("Started MongoDb with port: " + this.mongoDBContainer.getMappedPort(MONGO_PORT));

        }
        catch (Throwable ex)
        {
            assumeTrue("Cannot start MongoDBContainer", false);
        }
    }


    public void assertStaticMongoFlowProviderIsAvailable(LegendDefaultDatabaseAuthenticationFlowProvider flowProvider)
    {
        StaticDatasourceSpecification staticDatasourceSpecification = new StaticDatasourceSpecification();
        UserNamePasswordAuthenticationStrategy authenticationStrategy = new UserNamePasswordAuthenticationStrategy();
        RelationalDatabaseConnection relationalDatabaseConnection = new RelationalDatabaseConnection(staticDatasourceSpecification, authenticationStrategy, DatabaseType.MongoDB);
        relationalDatabaseConnection.type = DatabaseType.MongoDB;

        Optional<DatabaseAuthenticationFlow> flow = flowProvider.lookupFlow(relationalDatabaseConnection);
        assertTrue("static Mongo flow does not exist ", flow.isPresent());
    }

    @After
    public void cleanup()
    {
        this.mongoDBContainer.stop();
    }

    @Test
    public void testUser1CanConnectWithStaticUserNamePasswordConnectionAndFindOrAggregateMatchOnCollectionEmployees() throws Exception
    {

        NonRelationalClient client = mongoClientForTestUser1WithStaticUserNamePassword();

        Document aggregateDoc = client.executeNativeCommandAsJson(DB_USER_DATABASE, "{\n" +
                "  \"aggregate\": \"" + DB_USER_DB_EMPLOYEE_COLLECTION + "\",\n" +
                "  \"pipeline\": [\n" +
                "    {\n" +
                "      \"$match\": {}\n" +
                "    }\n" +
                "  ],\n" +
                "  \"cursor\": {}\n" +
                "}");


        assertEquals(1.0, aggregateDoc.get("ok"));

        Document findDoc = client.executeNativeCommandAsJson(DB_USER_DATABASE, "{\n" +
                "  \"find\": \"" + DB_USER_DB_EMPLOYEE_COLLECTION + "\"\n" +

                "}");

        assertEquals(1.0, findDoc.get("ok"));

    }

    @Test
    public void testUser2CannotAuthenticateWithStaticUserNamePasswordConnectionAndFindOrAggregateMatchOnCollectionEmployees() throws Exception
    {
        NonRelationalClient client = mongoClientForTestUser2WithStaticUserNamePassword();
        Exception e3 = Assert.assertThrows(RuntimeException.class, () -> client.executeNativeCommandAsJson(DB_USER_DATABASE, "{\n" +
                "  \"aggregate\": \"" + DB_USER_DB_EMPLOYEE_COLLECTION + "\",\n" +
                "  \"pipeline\": [\n" +
                "    {\n" +
                "      \"$match\": {}\n" +
                "    }\n" +
                "  ],\n" +
                "  \"cursor\": {}\n" +
                "}"));


        assertEquals(MongoSecurityException.class, e3.getCause().getClass());
        Assert.assertEquals("Exception authenticating MongoCredential{mechanism=SCRAM-SHA-1, userName='testUser2', source='userDatabase', password=<hidden>, mechanismProperties=<hidden>}", e3.getCause().getMessage());

    }


    private NonRelationalClient mongoClientForTestUser1WithStaticUserNamePassword()
    {
        StaticDatasourceSpecification mongoDatasourceSpecification = new StaticDatasourceSpecification();
        mongoDatasourceSpecification.host = "localhost";
        mongoDatasourceSpecification.port = this.mongoDBContainer.getMappedPort(MONGO_PORT);
        mongoDatasourceSpecification.databaseName = DB_USER_DATABASE;


        UserNamePasswordAuthenticationStrategy authSpec = new UserNamePasswordAuthenticationStrategy();
        authSpec.baseVaultReference = "mongodb.";
        authSpec.userNameVaultReference = DB_TEST_USER_1_USERNAME;
        authSpec.passwordVaultReference = DB_TEST_USER_1_PASSWORD;

        return new MongoDbClient(mongoDatasourceSpecification, authSpec);
    }

    private NonRelationalClient mongoClientForTestUser2WithStaticUserNamePassword()
    {
        StaticDatasourceSpecification mongoDatasourceSpecification = new StaticDatasourceSpecification();
        mongoDatasourceSpecification.host = "localhost";
        mongoDatasourceSpecification.port = this.mongoDBContainer.getMappedPort(MONGO_PORT);
        mongoDatasourceSpecification.databaseName = DB_USER_DATABASE;


        UserNamePasswordAuthenticationStrategy authSpec = new UserNamePasswordAuthenticationStrategy();
        authSpec.baseVaultReference = "mongodb.";
        authSpec.userNameVaultReference = DB_TEST_USER_2_USERNAME;
        authSpec.passwordVaultReference = DB_TEST_USER_2_PASSWORD;

        return new MongoDbClient(mongoDatasourceSpecification, authSpec);
    }

    private NonRelationalClient mongoClientForRootAdminWithStaticUserNamePassword()
    {
        StaticDatasourceSpecification mongoDatasourceSpecification = new StaticDatasourceSpecification();
        mongoDatasourceSpecification.host = "localhost";
        mongoDatasourceSpecification.port = this.mongoDBContainer.getMappedPort(MONGO_PORT);
        mongoDatasourceSpecification.databaseName = DB_ADMIN_DATABASE;

        UserNamePasswordAuthenticationStrategy authSpec = new UserNamePasswordAuthenticationStrategy();
        authSpec.baseVaultReference = "mongodb.";
        authSpec.userNameVaultReference = DB_ROOT_USERNAME;
        authSpec.passwordVaultReference = DB_ROOT_PASSWORD;

        return new MongoDbClient(mongoDatasourceSpecification, authSpec);
    }
}
