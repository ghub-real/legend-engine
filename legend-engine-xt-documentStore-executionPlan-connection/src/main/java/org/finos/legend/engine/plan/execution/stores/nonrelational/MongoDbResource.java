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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bson.Document;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalClient;
import org.slf4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Api(tags = "MongoDB - Utilities")
@Path("graphQl/v1/mongodb")
@Produces(MediaType.APPLICATION_JSON)
public class MongoDbResource
{
    private static final String DEFAULT_DATABASE_NAME = "my_database";
    private final NonRelationalClient nonRelationalClient;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("MongoDbResource");


    public MongoDbResource(NonRelationalClient mongoDbClient)
    {
        this.nonRelationalClient = mongoDbClient;
    }

    // MongoDB POC Helper APIs

    // re-creates collections persons and firms with sample data
    public void populateData()
    {
        try
        {
            this.recreatePersonsCollection();
            this.recreateFirmsCollection();
        }
        catch (JsonProcessingException e)
        {
            LOGGER.error("Failed to populate data against MongoDb", e);
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/defaultdb/collections")
    @ApiOperation(value = "Create new collection")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createCollection(@QueryParam("collectionName") String collectionName)
    {

        MongoDatabase database = this.getDefaultDB();
        database.createCollection(collectionName);

        return Response.ok().build();
    }

    @DELETE
    @Path("/defaultdb/collections")
    @ApiOperation(value = "Remove collection")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response dropCollection(@QueryParam("collectionName") String collectionName)
    {

        MongoDatabase database = this.getDefaultDB();
        database.getCollection(collectionName).drop();

        return Response.ok().build();
    }

    @POST
    @Path("/defaultdb/collections/empty")
    @ApiOperation(value = "Remove all items from collection")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response emptyCollection(@QueryParam("collectionName") String collectionName)
    {

        MongoDatabase database = this.getDefaultDB();
        BasicDBObject document = new BasicDBObject();

        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.deleteMany(document);

        return Response.ok().build();
    }

    @POST
    @Path("/defaultdb/collections/persons/empty")
    @ApiOperation(value = "Remove all items from persons collection")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response emptyPersonsCollection()
    {
        return this.emptyCollection("persons");
    }


    @POST
    @Path("/defaultdb/collections/firm/recreate")
    @ApiOperation(value = "Empty and recreate all items for firm collection")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response recreateFirmsCollection() throws JsonProcessingException
    {

        ArrayNode personsJson;
        try
        {
            personsJson = (ArrayNode) mapper.readTree(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("firms.json")));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to parse input file contents", e);
            throw new RuntimeException(e);
        }

        this.dropCollection("firm");
        return this.insertCollectionItemAsJson("firm", personsJson);
    }

    @POST
    @Path("/defaultdb/collections/person/recreate")
    @ApiOperation(value = "Empty and recreate all items for person collection")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response recreatePersonsCollection() throws JsonProcessingException
    {

        ArrayNode personsJson;
        try
        {
            personsJson = (ArrayNode) mapper.readTree(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("persons.json")));
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to parse input file contents", e);
            throw new RuntimeException(e);
        }

        this.dropCollection("person");
        return this.insertCollectionItemAsJson("person", personsJson);
    }

    @GET
    @Path("/defaultdb/collections/all")
    @ApiOperation(value = "Get all collection names")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAllCollectionNames()
    {

        MongoDatabase database = this.getDefaultDB();
        return Response.ok().entity(database.listCollectionNames()).build();
    }

    @GET
    @Path("/defaultdb/query")
    @ApiOperation(value = "Invoke aggregate query on database")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAllCollectionItemsWithQuery(@QueryParam("mongoQuery") String mongoQuery)
    {

        MongoDatabase database = this.getDefaultDB();
        List<String> results = executeCustomAggregationQueryWithCursor(database, mongoQuery);

        return Response.ok().entity(results).build();
    }

    @GET
    @Path("/defaultdb/collections/documents")
    @ApiOperation(value = "Get all collection documents")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAllCollectionItems(@QueryParam("collectionName") String collectionName)
    {

        MongoDatabase database = this.getDefaultDB();

        MongoCollection<Document> collection = database.getCollection(collectionName);

        List<String> documents = new ArrayList<>();
        try (MongoCursor<Document> cur = collection.find().iterator())
        {
            while (cur.hasNext())
            {
                Document doc = cur.next();
                List<Object> document = new ArrayList<>(doc.values());
                System.out.println(document);
                documents.add(document.toString());
            }
        }

        return Response.ok().entity(documents).build();
    }


    @POST
    @Path("/defaultdb/collections/documents/customJson")
    @ApiOperation(value = "Insert documents for a collection with items as json")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response insertCollectionItemAsJson(@QueryParam("collectionName") String collectionName, ArrayNode itemJson)
    {

        MongoDatabase database = this.getDefaultDB();

        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> docs = new ArrayList<>();

        itemJson.forEach(n ->
        {
            String json;
            try
            {
                json = mapper.writeValueAsString(n);
                docs.add(Document.parse(json));
            }
            catch (JsonProcessingException e)
            {
                LOGGER.error("Failed to parse item: ", e);
                throw new RuntimeException(e);
            }
        });
        collection.insertMany(docs);

        return Response.ok().build();
    }

    private MongoDatabase getDefaultDB()
    {
        return this.nonRelationalClient.getMongoDBClient().getDatabase(DEFAULT_DATABASE_NAME);
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


}
