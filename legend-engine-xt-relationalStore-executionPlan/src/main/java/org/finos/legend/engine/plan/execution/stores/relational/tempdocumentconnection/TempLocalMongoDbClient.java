package org.finos.legend.engine.plan.execution.stores.relational.tempdocumentconnection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.specification.MongoDBDatasourceSpecification;

import java.util.ArrayList;
import java.util.List;

public class TempLocalMongoDbClient {

    private static final String DEFAULT_DATABASE_NAME = "my_database";

    MongoClient client;

    private MongoDatabase getDefaultDB() {
        return this.client.getDatabase(DEFAULT_DATABASE_NAME);
    }


    public TempLocalMongoDbClient() {
        // handle custom config etc
        client = MongoClients.create("mongodb://localhost:27017");
    }

    public TempLocalMongoDbClient(MongoDBDatasourceSpecification dss) {
        client = MongoClients.create("mongodb://" + dss.host + ":" + dss.port);
    }


    private List<String> executeCustomAggregationQueryWithCursor(MongoDatabase database, String query) {

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


    public List<String> executeCustomAggregationQueryToDefaultDB(String mongoQuery) {

        MongoDatabase database = this.getDefaultDB();
        List<String> results = executeCustomAggregationQueryWithCursor(database, mongoQuery);

        return results;
    }

    public void close() {
        this.client.close();
    }
}

