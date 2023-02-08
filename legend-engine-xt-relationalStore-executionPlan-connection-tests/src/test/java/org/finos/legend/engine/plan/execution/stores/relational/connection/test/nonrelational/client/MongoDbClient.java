
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

package org.finos.legend.engine.plan.execution.stores.relational.connection.test.nonrelational.client;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.authentication.UserNamePasswordAuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.specification.StaticDatasourceSpecification;

public class MongoDbClient implements NonRelationalClient
{

    private final MongoClient mongoClient;

    public MongoDbClient(StaticDatasourceSpecification datasourceSpecification, UserNamePasswordAuthenticationStrategy authenticationStrategy)
    {

        String connectionString = "mongodb://" + authenticationStrategy.userNameVaultReference + ":" +
                authenticationStrategy.passwordVaultReference + "@" + datasourceSpecification.host + ":"
                + datasourceSpecification.port + "/" + datasourceSpecification.databaseName;

        this.mongoClient = MongoClients.create(connectionString);
    }

    public MongoClient getMongoDBClient()
    {
        return this.mongoClient;
    }


    public Document executeNativeCommandAsJson(String databaseName, String command) throws RuntimeException
    {

        MongoDatabase database = this.getDatabase(databaseName);
        try
        {
            Document bsonCmd = Document.parse(command);
            // Execute the native query
            return database.runCommand(bsonCmd);

        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to execute Mongo native query:\n" + command, e);
        }

    }

    private MongoDatabase getDatabase(String databaseName)
    {
        return this.mongoClient.getDatabase(databaseName);
    }


    public void shutDown()
    {
        this.mongoClient.close();
    }
}
