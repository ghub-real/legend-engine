package org.finos.legend.engine.connection.mongodb;

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


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Value;

@Value
public class LocalMongoDbClient
{

    MongoClient client;

    public LocalMongoDbClient()
    {
        // handle custom config etc
        client = MongoClients.create("mongodb://localhost:27017");
    }
}
