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

package org.finos.legend.engine.language.pure.grammar;

import org.antlr.v4.runtime.Vocabulary;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.DocumentStoreParser;
import org.finos.legend.engine.language.pure.grammar.test.TestGrammarParser;

import java.util.List;

public class TestDocumentStoreGrammarParser extends TestGrammarParser.TestGrammarParserTestSuite
{
    private static final String TEST_DOCUMENT_MODEL = "###Pure\n" +
            "Class Person \n" +
            "{ \n" +
            "   name: String[1]; \n" +
            "   city: String[1]; \n" +
            "} \n" +
            "###NonRelational \n" +
            "DocumentStore database::mongo::db \n" +
            "( \n" +
            "Collection Person\n" +
            "( \n" +
            "personName String, \n" +
            "_id ObjectId \n" +
            ") \n" +
            "Collection Firm\n" +
            "( \n" +
            "firmName String, \n" +
            "_id ObjectId PRIMARY KEY\n" +
            ") \n" +
            ") \n" +
            "###Mapping";

    @Override
    public Vocabulary getParserGrammarVocabulary()
    {
        return DocumentStoreParser.VOCABULARY;
    }

    @Override
    public String getParserGrammarIdentifierInclusionTestCode(List<String> keywords)
    {
        return "###Pure\n" +
                "Class Person \n" +
                "{ \n" +
                "   name: String[1]; \n" +
                "   city: String[1]; \n" +
                "} \n" +
                "###NonRelational \n" +
                "DocumentStore " + ListAdapter.adapt(keywords).makeString("::") + "::db \n" +
                "( \n" +
                "Collection Person\n" +
                "( \n" +
                "personName String, \n" +
                "_id ObjectId \n" +
                ") \n" +
                "Collection Firm\n" +
                "( \n" +
                "firmName String, \n" +
                "_id ObjectId PRIMARY KEY\n" +
                ") \n" +
                ") \n" +
                "###Mapping \n" +
                "Mapping mapping::document \n" +
                "( \n" +
                "Person: Pure \n" +
                "{ \n" +
                "~src Person \n" +
                "name: $src.name \n" +
                "} \n" +
                ") \n" +
                "###Runtime \n" +
                "Runtime runtime::document \n" +
                "{ \n" +
                "mappings: \n" +
                "[ \n" +
                "mapping::document \n" +
                "]; \n" +
                "}";
    }
}