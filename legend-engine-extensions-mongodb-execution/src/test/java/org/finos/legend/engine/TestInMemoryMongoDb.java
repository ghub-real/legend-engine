// Copyright 2020 Goldman Sachs
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

package org.finos.legend.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.bwaldvogel.mongo.MongoServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.CustomJSONPrettyPrinter;
import utils.InMemoryMongoDbServer;
import utils.MongoDbClient;

import static org.junit.Assert.assertEquals;
import static utils.TestUtils.resourceAsString;

public class TestInMemoryMongoDb
{
    private final ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new CustomJSONPrettyPrinter())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    private MongoDbClient mongoDbClient;
    private MongoServer mongoServer;

    @Before
    public void setUp()
    {
        this.mongoServer = InMemoryMongoDbServer.startServer();
        this.mongoDbClient = new MongoDbClient();

        String firmsJson = resourceAsString("mongoData/firms.json");
        this.mongoDbClient.insertCollectionItemAsJsonString("firm", firmsJson);
    }

    @After
    public void tearDown() throws Exception
    {
        this.mongoServer.shutdown();
        this.mongoDbClient.shutDown();
    }

    @Test
    public void testCanStartupInMemoryMongoLoadTestDataAndQueryResults()
    {
        String input = resourceAsString("empty_match_input.json");
        String expectedOutput = resourceAsString("empty_match_expected_query_result.json");

        this.testMongoQueryExecution(input, expectedOutput);
    }


    private void testMongoQueryExecution(String mongoQuery, String expectedJsonDataResult)
    {
        try
        {
            String results = this.mongoDbClient.executeNativeQuery(mongoQuery);
            assertEquals("The result data for MQL string (input) database execution is different from expected.",
                    mapper.readTree(expectedJsonDataResult).toPrettyString(),
                    mapper.readTree(results).toPrettyString());
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }


}