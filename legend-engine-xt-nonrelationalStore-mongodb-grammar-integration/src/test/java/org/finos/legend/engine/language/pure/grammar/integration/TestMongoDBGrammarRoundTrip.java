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

//TODO : Convert to round trip after getting composer setup.
public class TestMongoDBGrammarRoundTrip extends TestGrammarRoundtrip.TestGrammarRoundtripTestSuite
{

    @Test
    public void testEmptyMongoDBStoreGrammar()
    {
        test(
                "###Mapping\n" +
                "Mapping mongo::test::mapping::MongoDBMapping\n" +
                "(\n" +
                "  *meta::external::store::mongodb::showcase::domain::Person[Person]: MongoDB\n" +
                "  {\n" +
                "    ~mainCollection [meta::external::store::mongodb::showcase::store::PersonDatabase] PersonCollection\n" +
                "    ~binding meta::external::store::mongodb::showcase::store::PersonCollectionBinding\n" +
                "  }\n" +
                ")\n\n\n" +
                "###Runtime\n" +
                "Runtime mongo::test::runtime::MongoDBRuntime\n" +
                "{\n" +
                "  mappings:\n" +
                "  [\n" +
                "    mongo::test::mapping::MongoDBMapping\n" +
                "  ];\n" +
                "}\n" +
                "###MongoDB\n" +
                "Database test::testEmptyDatabase\n" +
                "(\n" +
                ")\n\n\n" +
                "###Service\n" +
                "Service test::currentService\n" +
                "{\n" +
                "  pattern: 'url/myUrl/';\n" +
                "  owners:\n" +
                "  [\n" +
                "    'ownerName'\n" +
                "  ];\n" +
                "  documentation: 'test';\n" +
                "  autoActivateUpdates: true;\n" +
                "  execution: Single\n" +
                "  {\n" +
                "    query: src: test::class[1]|$src.prop1;\n" +
                "    mapping: mongo::test::mapping::MongoDBMapping;\n" +
                "    runtime: test::runtime;\n" +
                "  }\n" +
                "  testSuites:\n" +
                "  [\n" +
                "    testSuite_1:\n" +
                "    {\n" +
                "      data:\n" +
                "      [\n" +
//                "        connections:\n" +
//                "        [\n" +
//                "          mongoDBConection:\n" +
//                "            ExternalFormat\n" +
//                "            #{\n" +
//                "              contentType: 'application/json'\n" +
//                "              data: '[]'\n" +
//                "            }#\n" +
//                "        ]\n" +
                "      ]\n" +
                "      tests:\n" +
                "      [\n" +
                "        test_1:\n" +
                "        {\n" +
                "          asserts:\n" +
                "          [\n\n" +
//                "            EqualToJson\n" +
//                "            #{\n" +
//                "              expected:\n" +
//                "                ExternalFormat\n" +
//                "                #{\n" +
//                "                  contentType: 'application/json';\n" +
//                "                  data: '[]';\n" +
//                "                }#\n" +
//                "            }#\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n");
    }

    @Test
    public void testSingleCollectionMongoDBStoreGrammar()
    {
        test("###MongoDB\n" +
                "Database test::testEmptyDatabase\n" +
                "(\n" +
                "  Collection Person\n" +
                "  (\n" +
                "    validationLevel: strict;\n" +
                "    validationAction: error;\n" +
                "    jsonSchema: {\n" +
                "      \"bsonType\": \"object\",\n" +
                "      \"title\": \"Record of Firm\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"bsonType\": \"string\",\n" +
                "          \"description\": \"name of the firm\",\n" +
                "          \"minLength\": 2\n" +
                "        }\n" +
                "      },\n" +
                "      \"additionalProperties\": false\n" +
                "    };\n" +
                "  )\n" +
                ")\n");
    }
}
