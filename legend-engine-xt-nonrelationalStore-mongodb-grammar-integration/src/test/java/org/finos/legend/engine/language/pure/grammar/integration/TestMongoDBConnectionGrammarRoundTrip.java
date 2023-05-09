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

package org.finos.legend.engine.language.pure.grammar.integration;

import org.finos.legend.engine.language.pure.grammar.test.TestGrammarRoundtrip;
import org.junit.Test;

public class TestMongoDBConnectionGrammarRoundTrip extends TestGrammarRoundtrip.TestGrammarRoundtripTestSuite
{

    @Test
    public void testMongoDBConnectionGrammar()
    {
        test("###Connection\n" +
                "MongoDBConnection test::testConnection\n" +
                "{\n" +
                "  database: legend_db;\n" +
                "  store: mongo::test::db;\n" +
                "  serverURLs: [localhost:27071];\n" +
                "  authentication: # UserPassword {\n" +
                "    username: 'mongo_ro';\n" +
                "    password: SystemPropertiesSecret\n" +
                "    {\n" +
                "      systemPropertyName: 'sys.prop.name';\n" +
                "    };\n" +
                "  }#;\n" +
                "}\n");
    }




    public void testMongoDBConnectionGrammarWithDifferentHostnames(String hostname)
    {
        test("###Connection\n" +
                "MongoDBConnection test::testConnection\n" +
                "{\n" +
                "  database: legend_db;\n" +
                "  store: mongo::test::db;\n" +
                "  serverURLs: [" + hostname + ":27071];\n" +
                "  authentication: # UserPassword {\n" +
                "    username: 'mongo_ro';\n" +
                "    password: SystemPropertiesSecret\n" +
                "    {\n" +
                "      systemPropertyName: 'sys.prop.name';\n" +
                "    };\n" +
                "  }#;\n" +
                "}\n");
    }

    @Test
    public void testMongoDBConnectionGrammarLocalhostWithHyphen2()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("localhost");

    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames1()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("host-subdomain456.com");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames2()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("localhost");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames3()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("host123-subdomain456.com");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames4()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("subdomain.examplehost.com");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames5()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("host123.examplehost.com");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames6()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("host123.examplehost.co.nz");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames7()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("host123-subdomain456.examplehost.co.in");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames8()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("host-name");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames9()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("f12345-001.ab.AB.com");
    }

    @Test
    public void testMongoDBConnectionGrammarWithDifferentHostnames10()
    {
        testMongoDBConnectionGrammarWithDifferentHostnames("fad12345df-001.aB.Ab.com");
    }

}
