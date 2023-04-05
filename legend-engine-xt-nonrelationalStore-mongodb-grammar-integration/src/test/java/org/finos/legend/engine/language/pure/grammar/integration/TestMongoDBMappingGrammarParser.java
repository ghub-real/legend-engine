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

import org.antlr.v4.runtime.Vocabulary;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.mapping.MongoDBMappingParserGrammar;
import org.finos.legend.engine.language.pure.grammar.test.TestGrammarParser;

import java.util.List;

public class TestMongoDBMappingGrammarParser extends TestGrammarParser.TestGrammarParserTestSuite
{
    @Override
    public Vocabulary getParserGrammarVocabulary()
    {
        return MongoDBMappingParserGrammar.VOCABULARY;
    }

    @Override
    public String getParserGrammarIdentifierInclusionTestCode(List<String> keywords)
    {
        return "###Mapping\n" +
                "Mapping " + ListAdapter.adapt(keywords).makeString("::") + "\n" +
                "(\n" +
                "  *meta::external::mongo::mapping[id1]: MongoDB\n" +
                "    {\n" +
                "      ~filter [mongo::test::db] activeRecords \n" +
                "      ~mainCollection [mongo::test::db] PersonRecord \n" +
                "      ~binding mongo::test::PersonBinding \n" +
                "    }\n" +
                ")";
    }
}
