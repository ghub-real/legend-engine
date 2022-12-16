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

package org.finos.legend.engine.language.pure.grammar.from;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.finos.legend.engine.language.grammar.from.antlr4.DocumentStoreLexer;
import org.finos.legend.engine.language.grammar.from.antlr4.DocumentStoreParser;
import org.finos.legend.engine.language.grammar.from.antlr4.connection.DocumentStoreConnectionLexer;
import org.finos.legend.engine.language.grammar.from.antlr4.connection.DocumentStoreConnectionParser;
import org.finos.legend.engine.language.pure.grammar.from.connection.ConnectionValueSourceCode;
import org.finos.legend.engine.language.pure.grammar.from.connection.DocumentStoreConnectionParseTreeWalker;
import org.finos.legend.engine.language.pure.grammar.from.extension.ConnectionValueParser;
import org.finos.legend.engine.language.pure.grammar.from.extension.SectionParser;
import org.finos.legend.engine.protocol.pure.v1.model.packagableElement.store.document.connection.DocumentStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.DefaultCodeSection;

import java.util.Collections;

public class DocumentStoreGrammarParserExtension implements IDocumentStoreGrammarParserExtension
{
    public static final String NAME = "NonRelational";
    public static final String SERVICE_STORE_MAPPING_ELEMENT_TYPE = "NonRelationalMapping";
    public static final String SERVICE_STORE_CONNECTION_TYPE = "DocumentStoreConnection";

    @Override
    public Iterable<? extends SectionParser> getExtraSectionParsers()
    {
        return Collections.singletonList(SectionParser.newParser(NAME, (sectionSourceCode, elementConsumer, context) ->
        {
            SourceCodeParserInfo parserInfo = getDocumentStoreParserInfo(sectionSourceCode);
            DefaultCodeSection section = new DefaultCodeSection();
            section.parserName = sectionSourceCode.sectionType;
            section.sourceInformation = parserInfo.sourceInformation;
            DocumentStoreParseTreeWalker walker = new DocumentStoreParseTreeWalker(parserInfo.walkerSourceInformation, elementConsumer, section);
            walker.visit((DocumentStoreParser.DefinitionContext) parserInfo.rootContext);
            return section;
        }));
    }

    @Override
    public Iterable<? extends ConnectionValueParser> getExtraConnectionParsers()
    {
        return Collections.singletonList(ConnectionValueParser.newParser(SERVICE_STORE_CONNECTION_TYPE, connectionValueSourceCode ->
        {
            SourceCodeParserInfo parserInfo = getDocumentStoreConnectionParserInfo(connectionValueSourceCode);
            DocumentStoreConnectionParseTreeWalker walker = new DocumentStoreConnectionParseTreeWalker(parserInfo.walkerSourceInformation);
            DocumentStoreConnection connectionValue = new DocumentStoreConnection();
            connectionValue.sourceInformation = connectionValueSourceCode.sourceInformation;
            walker.visitDocumentStoreConnectionValue((DocumentStoreConnectionParser.DefinitionContext) parserInfo.rootContext, connectionValue, connectionValueSourceCode.isEmbedded);
            return connectionValue;
        }));
    }

    private static SourceCodeParserInfo getDocumentStoreParserInfo(SectionSourceCode sectionSourceCode)
    {
        CharStream input = CharStreams.fromString(sectionSourceCode.code);
        ParserErrorListener errorListener = new ParserErrorListener(sectionSourceCode.walkerSourceInformation);
        DocumentStoreLexer lexer = new DocumentStoreLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        DocumentStoreParser parser = new DocumentStoreParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        return new SourceCodeParserInfo(sectionSourceCode.code, input, sectionSourceCode.sourceInformation, sectionSourceCode.walkerSourceInformation, lexer, parser, parser.definition());
    }

    private static SourceCodeParserInfo getDocumentStoreConnectionParserInfo(ConnectionValueSourceCode connectionValueSourceCode)
    {
        CharStream input = CharStreams.fromString(connectionValueSourceCode.code);
        ParserErrorListener errorListener = new ParserErrorListener(connectionValueSourceCode.walkerSourceInformation);
        DocumentStoreConnectionLexer lexer = new DocumentStoreConnectionLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        DocumentStoreConnectionParser parser = new DocumentStoreConnectionParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        return new SourceCodeParserInfo(connectionValueSourceCode.code, input, connectionValueSourceCode.sourceInformation, connectionValueSourceCode.walkerSourceInformation, lexer, parser, parser.definition());
    }
}
