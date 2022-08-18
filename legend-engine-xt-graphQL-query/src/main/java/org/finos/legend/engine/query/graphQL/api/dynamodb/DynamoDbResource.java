// Copyright 2022 Goldman Sachs
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

package org.finos.legend.engine.query.graphQL.api.dynamodb;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.engine.shared.core.operational.errorManagement.ExceptionTool;
import org.finos.legend.engine.shared.core.operational.logs.LoggingEventType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "GraphQL - DynamoDb Persist")
@Path("graphQl/v1/dynamodb")
@Produces(MediaType.APPLICATION_JSON)
public class DynamoDbResource
{
    private final DynamoDbTableUtils dynamoDbTableUtils;

    public DynamoDbResource(LocalDynamoDbClient localDynamoDbClient) {
        this.dynamoDbTableUtils = new DynamoDbTableUtils(localDynamoDbClient.getDynamoDbClient());
    }

    // DynamoDB POC Helper APIs

    @POST
    @Path("dynamoDbTables/recreateGenericTable")
    @ApiOperation( value = "Deletes all DynamoDb tables and create single generic table")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response recreateGenericTable() {
        this.dynamoDbTableUtils.recreateGenericTable();
        return Response.ok().build();
    }

    @POST
    @Path("dynamoDbTables/createGenericTable")
    @ApiOperation( value = "Creates single generic table")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createGenericTable() {
        this.dynamoDbTableUtils.createGenericTable();
        return Response.ok().build();
    }

    @GET
    @Path("dynamoDbTables/genericTable/item")
    @ApiOperation( value = "Get item from GenericTable")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getGenericTableItem(@QueryParam("itemType")  String itemType, @QueryParam("id") String id) {

        String partitionKeyValue = itemType + "#" + id;
        //"Firm#f567780c-5f4d-44d0-b9d8-c8dc250d332a"
        List<Map<String,Object>> items = this.dynamoDbTableUtils.queryTable("GenericTable", partitionKeyValue);
        return Response.ok().entity(items).build();
    }

    @GET
    @Path("dynamoDbTables/genericTable/itemWithPartiQ")
    @ApiOperation( value = "Get item from GenericTable using PartiQ SQL-like API")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getGenericTableItemPartiQ(@QueryParam("itemType")  String itemType, @QueryParam("id") String id) {

        String partitionKeyValue = itemType + "#" + id;
        List<Map<String,Object>> items = this.dynamoDbTableUtils.queryTablePartiQ("GenericTable", partitionKeyValue);
        return Response.ok().entity(items).build();
    }

    @GET
    @Path("dynamoDbTables/all")
    @ApiOperation( value = "Get all DynamoDb tables")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response getAllTables() {

        ListTablesResponse existingTables = this.dynamoDbTableUtils.getAllTables();

        try {
            return Response.ok().entity(existingTables.toString()).build();
        } catch (Exception e) {
           return ExceptionTool.exceptionManager(e, LoggingEventType.CATCH_ALL, null);
        }
    }


    @GET
    @Path("dynamoDbTables/genericTable")
    @ApiOperation( value = "Describe Generic table")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response describeGenericTable() {
        return Response.ok().entity(this.dynamoDbTableUtils.describeGenericTable()).build();
    }

    @DELETE
    @Path("dynamoDbTables/all")
    @ApiOperation( value = "Delete all DynamoDb tables")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response deleteAllTables() {
    this.dynamoDbTableUtils.deleteAllTables();
     return Response.ok().build();
    }


    @PUT
    @Path("dynamoDbTables/putFirm")
    @ApiOperation( value = "Put firm in DynamoDb table")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response putItem(String firmName) {

        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put("PK", AttributeValue.builder().s("firm#" + firmName).build());
        attributes.put("SK", AttributeValue.builder().s("firm#" + firmName).build());
        attributes.put("name", AttributeValue.builder().s(firmName).build());

        this.dynamoDbTableUtils.putItem("GenericTable", attributes);
        return Response.ok().build();
    }

}
