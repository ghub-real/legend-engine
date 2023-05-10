package org.finos.legend.engine.language.pure.grammar.integration;

import org.finos.legend.engine.language.pure.compiler.test.TestCompilationFromGrammar;
import org.junit.Test;

public class TestMongoDBAllCompiler extends TestCompilationFromGrammar.TestCompilationFromGrammarTestSuite
{

    @Override
    protected String getDuplicatedElementTestCode()
    {
        return null;
    }

    @Override
    protected String getDuplicatedElementTestExpectedErrorMessage()
    {
        return null;
    }

    @Test
    public void testMongoDBDefinition()
    {
        test(TestMongoDBCompilerUtil.MODEL_PLUS_BINDING +
                TestMongoDBCompilerUtil.SAMPLE_STORE +
                "###Mapping\n" +
                "Mapping mongo::test::mapping::MongoDBMapping\n" +
                "(\n" +
                "  *meta::external::store::mongodb::showcase::domain::Person[Person]: MongoDB\n" +
                "  {\n" +
                "    ~mainCollection [meta::external::store::mongodb::showcase::store::PersonDatabase] PersonCollection\n" +
                "    ~binding meta::external::store::mongodb::showcase::store::PersonCollectionBinding\n" +
                "  }\n" +
                ")\n\n\n" +
                "###Connection\n" +
                "MongoDBConnection mongo::test::connection::MongoDBConnection\n" +
                "{\n" +
                "  database: userDatabase;\n" +
                "  store: meta::external::store::mongodb::showcase::store::PersonDatabase;\n" +
                "  serverURLs: [localhost:12345];\n" +
                "  authentication: # UserPassword {\n" +
                "    username: 'sa';\n" +
                "    password: SystemPropertiesSecret\n" +
                "    {\n" +
                "      systemPropertyName: 'password';\n" +
                "    };\n" +
                "  }#;\n" +
                "}\n" +
                "###Runtime\n" +
                "Runtime mongo::test::runtime::MongoDBRuntime\n" +
                "{\n" +
                "  mappings:\n" +
                "  [\n" +
                "    mongo::test::mapping::MongoDBMapping\n" +
                "  ];\n" +
                "  connections:\n" +
                "  [\n" +
                "    meta::external::store::mongodb::showcase::store::PersonDatabase:\n" +
                "    [\n" +
                "      connection_1: mongo::test::connection::MongoDBConnection\n" +
                "    ]\n" +
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
                "    query: |meta::external::store::mongodb::showcase::domain::Person.all()->graphFetch(#{meta::external::store::mongodb::showcase::domain::Person{firstName}}#)->serialize(#{meta::external::store::mongodb::showcase::domain::Person{firstName}}#);\n" +
                "    mapping: mongo::test::mapping::MongoDBMapping;\n" +
                "    runtime: mongo::test::runtime::MongoDBRuntime;\n" +
                "  }\n" +
                "  testSuites:\n" +
                "  [\n" +
                "    testSuite_1:\n" +
                "    {\n" +
                "      data:\n" +
                "      [\n" +
                "        connections:\n" +
                "        [\n" +
                "          mongoDBConection:\n" +
                "            ExternalFormat\n" +
                "            #{\n" +
                "              contentType: 'application/json'\n" +
                "              data: '[]'\n" +
                "            }#\n" +
                "        ]\n" +
                "      ]\n" +
                "      tests:\n" +
                "      [\n" +
                "        test_1:\n" +
                "        {\n" +
                "          asserts:\n" +
                "          [\n\n" +
                "            EqualToJson\n" +
                "            #{\n" +
                "              expected:\n" +
                "                ExternalFormat\n" +
                "                #{\n" +
                "                  contentType: 'application/json';\n" +
                "                  data: '[]';\n" +
                "                }#\n" +
                "            }#\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n");
    }
}
