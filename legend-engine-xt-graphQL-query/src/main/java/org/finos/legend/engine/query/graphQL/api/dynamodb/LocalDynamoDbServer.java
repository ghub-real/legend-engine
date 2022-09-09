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

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import lombok.Data;
import org.slf4j.Logger;

import java.io.IOException;

@Data
public class LocalDynamoDbServer {


    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LocalDynamoDbServer.class);

     private DynamoDBProxyServer server;
     private String runningPort;

    public LocalDynamoDbServer()  {

        // start up dynamoDB instance as sharedDB more info: https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.UsageNotes.html
        // data is saved on disk in current directory to a file named shared-local-instance.db under /legend-engine

        // note on M1 sillicon macs the native libs generated with install via maven plugin won't work
        // add to follow this and add an ARM64 lib version of sqlite https://taint.org/2022/02/09/183535a.html

        try {
            System.setProperty("sqlite4java.library.path", "/Users/renu/finos/legend-engine/legend-engine-xt-graphQL-query/native-libs");
            this.runningPort = getAvailablePort();
            LOGGER.info("Starting DynamoDB server on port {}", runningPort);
            this.server = ServerRunner.createServerFromCommandLineArgs(
                    new String[]{"-sharedDb", "-port", this.runningPort});

            this.server.start();
        } catch ( Exception e) {
            LOGGER.error("Failed to start up DynamoDB instance, {[]}", e);
        }

    }

    public String getAvailablePort() throws IOException {
        return "8000";
//        ServerSocket serverSocket = new ServerSocket(0);
//        return String.valueOf(serverSocket.getLocalPort());
    }
}
