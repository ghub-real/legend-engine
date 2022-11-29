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
import lombok.Value;
import org.bson.Document;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;

import java.util.ArrayList;
import java.util.List;

@Value
public class LocalMongoDbClient implements NonRelationalClient
{
    private static final String DEFAULT_DATABASE_NAME = "mongo_db";
    MongoClient client;
    private final String clientUri;

    public LocalMongoDbClient()
    {
        this("mongodb://localhost:27017");
    }

    public LocalMongoDbClient(String uri)
    {
        // handle custom config etc
        clientUri = uri;
        client = MongoClients.create(clientUri);
    }

    @Override
    public MongoClient getMongoDBClient()
    {
        return client;
    }

    private MongoDatabase getDefaultDB()
    {
        return this.client.getDatabase(DEFAULT_DATABASE_NAME);
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


            docs.forEach(doc -> res.add(doc.toString()));
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

        return res;
    }


    public List<String> executeNativeQuery(String mongoQuery)
    {

        MongoDatabase database = this.getDefaultDB();
        List<String> results = executeNativeQueryWithCursor(database, mongoQuery);

        return results;
    }


}
