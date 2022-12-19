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
import org.finos.legend.engine.language.pure.grammar.from.antlr4.DocumentStoreLexer;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.DocumentStoreParser;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.DocumentStoreConnectionLexer;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.DocumentStoreConnectionParser;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.authentication.AuthenticationStrategyLexerGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.authentication.AuthenticationStrategyParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.datasource.DataSourceSpecificationLexerGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.datasource.DataSourceSpecificationParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.authentication.AuthenticationStrategyParseTreeWalker;
import org.finos.legend.engine.language.pure.grammar.from.authentication.AuthenticationStrategySourceCode;
import org.finos.legend.engine.language.pure.grammar.from.connection.ConnectionValueSourceCode;
import org.finos.legend.engine.language.pure.grammar.from.connection.DocumentStoreConnectionParseTreeWalker;
import org.finos.legend.engine.language.pure.grammar.from.datasource.DataSourceSpecificationParseTreeWalker;
import org.finos.legend.engine.language.pure.grammar.from.datasource.DataSourceSpecificationSourceCode;
import org.finos.legend.engine.language.pure.grammar.from.extension.ConnectionValueParser;
import org.finos.legend.engine.language.pure.grammar.from.extension.SectionParser;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DocumentStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.DefaultCodeSection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategy;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class DocumentStoreGrammarParserExtension implements IDocumentStoreGrammarParserExtension
{
    public static final String NAME = "NonRelational";
    public static final String DOCUMENT_STORE_MAPPING_ELEMENT_TYPE = "NonRelationalMapping";
    public static final String DOCUMENT_STORE_CONNECTION_TYPE = "DocumentStoreConnection";

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
        return Collections.singletonList(ConnectionValueParser.newParser(DOCUMENT_STORE_CONNECTION_TYPE, connectionValueSourceCode ->
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

    @Override
    public List<Function<DataSourceSpecificationSourceCode, DatasourceSpecification>> getExtraDataSourceSpecificationParsers()
    {
        return Collections.singletonList(code ->
        {
            DataSourceSpecificationParseTreeWalker walker = new DataSourceSpecificationParseTreeWalker();

            switch (code.getType())
            {
                case "MongoDB":
                case "Static":
                    return parseDataSourceSpecification(code, p -> walker.visitStaticDatasourceSpecification(code, p.staticDatasourceSpecification()));
                default:
                    return null;
            }
        });
    }

    @Override
    public List<Function<AuthenticationStrategySourceCode, AuthenticationStrategy>> getExtraAuthenticationStrategyParsers()
    {
        return Collections.singletonList(code ->
        {
            AuthenticationStrategyParseTreeWalker walker = new AuthenticationStrategyParseTreeWalker();

            switch (code.getType())
            {
                case "DefaultMongo":
                case "Test":
                    return parseAuthenticationStrategy(code, p -> walker.visitTestDatabaseAuthenticationStrategy(code, p.testDBAuth()));
                default:
                    return null;
            }
        });
    }

    private DatasourceSpecification parseDataSourceSpecification(DataSourceSpecificationSourceCode code, Function<DataSourceSpecificationParserGrammar, DatasourceSpecification> func)
    {
        CharStream input = CharStreams.fromString(code.getCode());
        ParserErrorListener errorListener = new ParserErrorListener(code.getWalkerSourceInformation());
        DataSourceSpecificationLexerGrammar lexer = new DataSourceSpecificationLexerGrammar(input);
        DataSourceSpecificationParserGrammar parser = new DataSourceSpecificationParserGrammar(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        return func.apply(parser);
    }

    private AuthenticationStrategy parseAuthenticationStrategy(AuthenticationStrategySourceCode code, Function<AuthenticationStrategyParserGrammar, AuthenticationStrategy> func)
    {
        CharStream input = CharStreams.fromString(code.getCode());
        ParserErrorListener errorListener = new ParserErrorListener(code.getWalkerSourceInformation());
        AuthenticationStrategyLexerGrammar lexer = new AuthenticationStrategyLexerGrammar(input);
        AuthenticationStrategyParserGrammar parser = new AuthenticationStrategyParserGrammar(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        return func.apply(parser);
    }
}
