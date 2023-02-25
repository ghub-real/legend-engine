// Copyright 2023 Goldman Sachs
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

package org.finos.legend.engine.language.mongodb.schema.grammar.from;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.finos.legend.engine.language.mongodb.schema.grammar.to.MongoDBSchemaJsonComposer;
import org.finos.legend.engine.protocol.mongodb.schema.metamodel.MongoDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@RunWith(Parameterized.class)
public class TestSchemaParser
{

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("Alloy Execution Server");
    private final String inputJsonFile;

    public TestSchemaParser(String inputQueryFile)
    {
        inputJsonFile = inputQueryFile;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]{
                {"json/schema/schema_def_1.json"},
                {"json/schema/schema_def_2.json"},
        });
    }

    public ObjectMapper getObjectMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MongoDatabase.class, new MongoDBSchemaDeserializer());
        mapper.registerModule(module);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter());
        return mapper;
    }


    @Test
    public void testValidSchemaFile() throws Exception
    {
        URL url = Objects.requireNonNull(getClass().getClassLoader().getResource(this.inputJsonFile));
        String inputSchema = new String(Files.readAllBytes(Paths.get(url.toURI())), StandardCharsets.UTF_8);

        MongoDBSchemaParseTreeWalker parser = MongoDBSchemaParseTreeWalker.newInstance();
        MongoDatabase dbSchema = parser.parseDocument(inputSchema);
        // Pretty print input to canonical json format for test assertions
        String expected = getObjectMapper().readTree(inputSchema).toPrettyString();

        //Compose back to String
        MongoDBSchemaJsonComposer schemaComposer = MongoDBSchemaJsonComposer.newInstance();
        String schemaComposerOutput = schemaComposer.renderDocument(dbSchema);

        // Pretty print output from composoer for test assertions
        String actual = getObjectMapper().readTree(schemaComposerOutput).toPrettyString();

        Assert.assertEquals(expected, actual);
    }

    /*
    @Test
    public void testSingleNestedObjectSchema() throws Exception
    {
        URL url = Objects.requireNonNull(getClass().getClassLoader().getResource("json/schema/schema_def_1.json"));
        String inputJson = new String(Files.readAllBytes(Paths.get(url.toURI())), StandardCharsets.UTF_8);
        MongoDBSchemaParseTreeWalker parser = MongoDBSchemaParseTreeWalker.newInstance();
        MongoDatabase db = parser.parseDocument(inputJson);
        Assert.assertEquals("my_database_1", db.name);
    }

    @Test
    public void testNestedObjectInArray() throws Exception
    {
        URL url = Objects.requireNonNull(getClass().getClassLoader().getResource("json/schema/schema_def_2.json"));
        String inputJson = new String(Files.readAllBytes(Paths.get(url.toURI())));
        MongoDBSchemaParseTreeWalker parser = MongoDBSchemaParseTreeWalker.newInstance();
        MongoDatabase db = parser.parseDocument(inputJson);
        Assert.assertEquals("my_database_2", db.name);
//        Assert.assertEquals(inputJson, getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(db));
    }

    @Test
    public void testMultiNestedObjectSchema() throws Exception
    {
        URL url = Objects.requireNonNull(getClass().getClassLoader().getResource("json/schema/schema_def_3.json"));
        String inputJson = new String(Files.readAllBytes(Paths.get(url.toURI())));
        MongoDBSchemaParseTreeWalker parser = MongoDBSchemaParseTreeWalker.newInstance();
        MongoDatabase db = parser.parseDocument(inputJson);
        Assert.assertEquals("my_database_3", db.name);
    }
 */
}
