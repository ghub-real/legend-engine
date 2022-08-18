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

import lombok.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Value
public class LocalDynamoDbClient {

    DynamoDbClient dynamoDbClient;
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public LocalDynamoDbClient(String port)  {
        // start up DynamoDB client instance

        URI uri = URI.create("http://localhost:" + port);

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "dummy_access",
                "dummy_key"
        );

        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_NORTH_1).
                credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(uri)
                .build();

        this.dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }
}
