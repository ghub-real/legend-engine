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

package org.finos.legend.engine.plan.execution.stores.document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification;

import java.util.ArrayList;
import java.util.List;

public class LocalMongoDBClient
{
    private static final String DEFAULT_DATABASE_NAME = "my_database";

    MongoClient client;

    public LocalMongoDBClient()
    {
        // handle custom config etc
        client = MongoClients.create("mongodb://localhost:27017");
    }


    public LocalMongoDBClient(MongoDBDatasourceSpecification dss)
    {
        client = MongoClients.create("mongodb://" + dss.host + ":" + dss.port);
    }

    private MongoDatabase getDefaultDB()
    {
        return this.client.getDatabase(DEFAULT_DATABASE_NAME);
    }

    private List<String> executeCustomAggregationQueryWithCursor(MongoDatabase database, String query)
    {

        Document bsonCmd = Document.parse(query);

        // Execute the native query
        Document result = database.runCommand(bsonCmd);
        Document cursor = (Document) result.get("cursor");
        List<Document> docs = (List<Document>) cursor.get("firstBatch");
        docs.forEach(System.out::println);

        List<String> res = new ArrayList<>();

        docs.forEach(doc -> res.add(doc.toString()));
        return res;
    }


    public List<String> executeCustomAggregationQueryToDefaultDB(String mongoQuery)
    {

        MongoDatabase database = this.getDefaultDB();
        List<String> results = executeCustomAggregationQueryWithCursor(database, mongoQuery);

        return results;
    }

    public void close()
    {
        this.client.close();
    }


}
