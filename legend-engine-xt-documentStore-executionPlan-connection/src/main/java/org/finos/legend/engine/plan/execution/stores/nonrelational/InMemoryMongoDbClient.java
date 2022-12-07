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

import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class InMemoryMongoDbClient extends MongoDbClient
{
    private final MongoServer mongoServer;

    public InMemoryMongoDbClient(int port)
    {
        System.out.println("Starting setup of dynamic connection for in-memory database: Mongo");
        this.mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind(DEFAULT_MONGO_HOSTNAME, port);
        this.mongoClient = MongoClients.create("mongodb://" + DEFAULT_MONGO_HOSTNAME + ":" + port);
        System.out.println("Completed setup for in-memory database: Mongo on port: " + port + " and address: " + DEFAULT_MONGO_HOSTNAME);
    }

    public InMemoryMongoDbClient()
    {
        this(DEFAULT_MONGO_PORT);
    }

    public void shutDown()
    {
        this.mongoClient.close();
        this.mongoServer.shutdown();
    }
}