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

import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collectors;

//@DynamoDBTypeConverted
public class DynamoDbTableUtils {
    private final DynamoDbClient ddb;

    public DynamoDbTableUtils(DynamoDbClient db) {
        this.ddb = db;
    }

    public void recreateGenericTable() {
        this.deleteAllTables();
        this.createGenericTable();
    }

    public ListTablesResponse getAllTables() {
        return this.ddb.listTables();
    }

    // example from https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb-tables.html
    public List<Map<String, Object>> describeGenericTable() {
        DescribeTableRequest request = DescribeTableRequest.builder()
                .tableName("GenericTable")
                .build();
        try {

            TableDescription tableInfo =
                    ddb.describeTable(request).table();

            if (tableInfo != null) {
                System.out.format("Table name  : %s\n",
                        tableInfo.tableName());
                System.out.format("Table ARN   : %s\n",
                        tableInfo.tableArn());
                System.out.format("Status      : %s\n",
                        tableInfo.tableStatus());
                System.out.format("Item count  : %d\n",
                        tableInfo.itemCount().longValue());
                System.out.format("Size (bytes): %d\n",
                        tableInfo.tableSizeBytes().longValue());

                ProvisionedThroughputDescription throughputInfo =
                        tableInfo.provisionedThroughput();
                System.out.println("Throughput");
                System.out.format("  Read Capacity : %d\n",
                        throughputInfo.readCapacityUnits().longValue());
                System.out.format("  Write Capacity: %d\n",
                        throughputInfo.writeCapacityUnits().longValue());

                List<AttributeDefinition> attributes =
                        tableInfo.attributeDefinitions();
                System.out.println("Attributes");

                for (AttributeDefinition a : attributes) {
                    System.out.format("  %s (%s)\n",
                            a.attributeName(), a.attributeType());
                }

                return scanItems("GenericTable");
            }
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
        return null;
    }

    public List<Map<String, AttributeValue>> getAllItemsForTable(String tableName) {

        ScanRequest scanRequest = ScanRequest.builder().tableName(tableName).build();

        ScanResponse response = this.ddb.scan(scanRequest);

        return response.items();
    }

    public void deleteAllTables() {

        ListTablesResponse listTablesResponse = this.ddb.listTables();

        List<String> existingTables = listTablesResponse.tableNames();

        System.out.println("Found " + existingTables.size() + " tables to delete in DynamoDB");

        try {
            existingTables.forEach(tableName -> ddb.deleteTable(DeleteTableRequest.builder().tableName(tableName).build()));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public void createGenericTable() {

        List<KeySchemaElement> employeeTableKeys = new ArrayList<>();
        employeeTableKeys.add(KeySchemaElement.builder().keyType(KeyType.HASH).attributeName("PK").build());
        employeeTableKeys.add(KeySchemaElement.builder().keyType(KeyType.RANGE).attributeName("SK").build());

        List<AttributeDefinition> employeeTableAttr = new ArrayList<>();
        employeeTableAttr.add(AttributeDefinition.builder().attributeType(ScalarAttributeType.S).attributeName("PK").build());
        employeeTableAttr.add(AttributeDefinition.builder().attributeType(ScalarAttributeType.S).attributeName("SK").build());


        try {
            this.createTable("GenericTable", employeeTableKeys, employeeTableAttr);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }


    public List<Map<String,Object>> queryTable(String tableName, String partitionKeyVal) {

        String partitionKeyName = "PK";
        // not actually needed, as it will always be PK just as an example if PK is a reserved keyword in the future
        String partitionAlias = "pk";

        // Set up an alias for the partition key name in case it's a reserved word.
        HashMap<String,String> attrNameAlias = new HashMap<>();
        attrNameAlias.put( "#" + partitionAlias, partitionKeyName);

        // Set up mapping of the partition name with the value.
         Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":"+ partitionKeyName, AttributeValue.builder().s(partitionKeyVal).build());

        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#" + partitionAlias + " = :" + partitionKeyName)
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();

        try {
            QueryResponse response = ddb.query(request);

             System.out.println("Found " + response.count() + " matches");

           List<Map<String,Object>> items = response.items().stream().map(DynamoDbTableUtils::mapToJson).collect(Collectors.toList());
            return items;

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }


    // partiQL POC
    // https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/ScenarioPartiQ.java
    public List<Map<String,Object>> queryTablePartiQ(String tableName, String partitionKeyVal) {

        String sqlStatement = "SELECT * FROM " + tableName + " WHERE PK =?";

        List<AttributeValue> parameters = new ArrayList<>();
        AttributeValue att1 = AttributeValue.builder()
                .s(partitionKeyVal)
                .build();

        parameters.add(att1);

        try {
            ExecuteStatementResponse response = executeStatementRequest(ddb, sqlStatement, parameters);
            System.out.println("ExecuteStatement successful: "+ response.toString());


            List<Map<String,Object>> items = response.items().stream().map(DynamoDbTableUtils::mapToJson).collect(Collectors.toList());
            return items;
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static ExecuteStatementResponse executeStatementRequest(DynamoDbClient ddb, String statement, List<AttributeValue> parameters ) {
        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .statement(statement)
                .parameters(parameters)
                .build();

        return ddb.executeStatement(request);
    }

    public void createTable(String tableName, List<KeySchemaElement> keySchemas,
                            List<AttributeDefinition> keyAttributes) {

        ProvisionedThroughput throughput = ProvisionedThroughput.builder()
                .readCapacityUnits(10L).writeCapacityUnits(10L).build();

        this.ddb.createTable(CreateTableRequest.builder().tableName(tableName).keySchema(keySchemas)
                .attributeDefinitions(keyAttributes).provisionedThroughput(throughput).build());
    }

    @Builder
    @Data
    public static class PersistedItemReturn {
        private String tableName;
        private String pk;
        private String sk;
    }

    @Builder
    @Data
    public static class PersistedMutationReturn {
        private  PersistedItemReturn persistedItem;
        private JsonNode json;
        private String rootClass;
        private String id;
    }

    public PersistedItemReturn putItem(String tableName, Map<String, AttributeValue> attributes) {

        if (!attributes.containsKey("PK")) {
            throw new RuntimeException("Cannot put item without PK");
        }
        if (!attributes.containsKey("SK")) {
            throw new RuntimeException("Cannot put item without SK");
        }

        this.ddb.putItem(PutItemRequest.builder().tableName(tableName).item(attributes).build());

        PersistedItemReturn resp = PersistedItemReturn.builder().tableName(tableName).pk(attributes.get("PK").toString()).sk(attributes.get("SK").toString()).build();

        return resp;
    }


    public PersistedItemReturn putItem(String tableName, Map<String, AttributeValue> attributes, String PK, String SK) {

        if (!attributes.containsKey("PK")) {
            throw new RuntimeException("Cannot put item without PK");
        }
        if (!attributes.containsKey("SK")) {
            throw new RuntimeException("Cannot put item without SK");
        }

        this.ddb.putItem(PutItemRequest.builder().tableName(tableName).item(attributes).build());

        PersistedItemReturn resp = PersistedItemReturn.builder().tableName(tableName).pk(attributes.get("PK").toString()).sk(attributes.get("SK").toString()).build();

        return resp;
    }

    public  Map<String, AttributeValue> getItemAttributes(ObjectNode node ) {

        Iterator<Map.Entry<String, JsonNode>> iter = node.fields();

        Map<String, AttributeValue> attributes = new HashMap<>();

        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            System.out.println();
            if (entry.getValue().isTextual()) {
                attributes.put(entry.getKey(), AttributeValue.builder().s(entry.getValue().textValue()).build());
            }
        }

        return attributes;

    }

 // objectType here would be Firm or hash of somepath::in::pure::domain::Firm that we can add to the PK/SK
    public PersistedItemReturn putJsonNode(String tableName, String objectType, ObjectNode node) {

        if (node.get("PK") == null) {
            throw new RuntimeException("Cannot put node without PK");
        }
        if (node.get("SK") == null) {
            throw new RuntimeException("Cannot put node without PK");
        }

        Map<String, AttributeValue> attributes = getItemAttributes(node);

        if (!attributes.isEmpty()) {
            return this.putItem(tableName, attributes);
        }

        return null;
    }

    // dynamoDB used to have some utils classes ( in SDK 1 ) to serialize/deserialize, let's try to find what the
    // pattern is today instead of custom logic for this
    public static Map<String, Object> mapToJson(Map<String, AttributeValue> keyValueMap) {
        Map<String, Object> finalKeyValueMap = new HashMap();
        for (Map.Entry<String, AttributeValue> entry : keyValueMap.entrySet()) {
            if (entry.getValue().m() != null) {
                if (entry.getValue().n() != null) {
                    finalKeyValueMap.put('"' + entry.getKey() + '"', '"' + entry.getValue().n() + '"');
                } else {
                    if (entry.getValue().s() != null) {
                        finalKeyValueMap.put('"' + entry.getKey() + '"', '"' + entry.getValue().s() + '"');
                    } else {
                        if (entry.getValue().l() != null) {
                            for (int i = 0; i < entry.getValue().l().size(); i++)
                                finalKeyValueMap.put('"' + entry.getKey() + '"', mapToJson(entry.getValue().l().get(i).m()));
                        }
                    }
                }
            } else {
                finalKeyValueMap.put('"' + entry.getKey() + '"', mapToJson(entry.getValue().m()));
            }
        }
        return finalKeyValueMap;
    }

    public List<Map<String, Object>> scanItems(String tableName) {
        List<Map<String, Object>> tableItems = new ArrayList<>();
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .build();

            ScanResponse response = this.ddb.scan(scanRequest);

            for (Map<String, AttributeValue> item : response.items()) {
                Set<String> keys = item.keySet();
                Map<String, Object> convertedItem = mapToJson(item);
                tableItems.add(convertedItem);
                for (String key : keys) {
                    System.out.println("The key name is " + key + "\n");
                    System.out.println("The value is " + item.get(key).s());
                }
            }

        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return tableItems;
    }
}
