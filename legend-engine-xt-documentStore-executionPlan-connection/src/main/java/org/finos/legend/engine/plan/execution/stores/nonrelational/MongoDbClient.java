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

package org.finos.legend.engine.plan.execution.stores.nonrelational;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MongoDbClient implements NonRelationalClient
{

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MongoDbClient.class);
    private final MongoClient mongoClient;
    private static final String DEFAULT_MONGO_HOSTNAME = "localhost";
    private static final int DEFAULT_MONGO_PORT = 27017;
    private static final String DEFAULT_DATABASE_NAME = "my_database";
    private static final String CONNECTION_STRING_TEMPLATE = "mongodb://%s:%s";

    public MongoDbClient()
    {
        this("mongodb://" + DEFAULT_MONGO_HOSTNAME + ":" + DEFAULT_MONGO_PORT);
    }

    public MongoDbClient(int port)
    {
        this(String.format(CONNECTION_STRING_TEMPLATE, DEFAULT_MONGO_HOSTNAME, port));
    }

    public MongoDbClient(String hostname, int port)
    {
        this(String.format(CONNECTION_STRING_TEMPLATE, hostname, port));
    }

    public MongoDbClient(String connectionString)
    {
        this.mongoClient = MongoClients.create(connectionString);
    }

    @Override
    public MongoClient getMongoDBClient()
    {
        return this.mongoClient;
    }

    private List<String> executeNativeQueryWithCursor(MongoDatabase database, String query)
    {
        List<String> res = new ArrayList<>();
        try
        {
            Document bsonCmd = Document.parse(query);

            // Execute the native query
            Document result = database.runCommand(bsonCmd);
            Document cursor = (Document) result.get("cursor");
            List<Document> docs = (List<Document>) cursor.get("firstBatch");
            docs.forEach(System.out::println);


            docs.forEach(doc -> res.add(doc.toJson()));
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to execute Mongo native query {}", e);
        }

        return res;
    }

    private MongoDatabase getDefaultDB()
    {
        return this.mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
    }

    public List<String> executeNativeQuery(String mongoQuery)
    {
        MongoDatabase database = this.getDefaultDB();
        List<String> results = executeNativeQueryWithCursor(database, mongoQuery);

        return results;
    }

    @Override
    public void shutDown()
    {
        this.mongoClient.close();
    }
}
