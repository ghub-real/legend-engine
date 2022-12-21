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

package org.finos.legend.engine.language.pure.grammar.from.connection;

import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.grammar.from.IDocumentStoreGrammarParserExtension;
import org.finos.legend.engine.language.pure.grammar.from.ParseTreeWalkerSourceInformation;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserUtility;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.DocumentStoreConnectionParser;
import org.finos.legend.engine.language.pure.grammar.from.authentication.AuthenticationStrategySourceCode;
import org.finos.legend.engine.language.pure.grammar.from.datasource.DataSourceSpecificationSourceCode;
import org.finos.legend.engine.protocol.pure.v1.model.SourceInformation;
import org.finos.legend.engine.protocol.pure.v1.model.context.EngineErrorType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DocumentStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.shared.core.operational.errorManagement.EngineException;

import java.util.Collections;
import java.util.List;

public class DocumentStoreConnectionParseTreeWalker
{

    private final ParseTreeWalkerSourceInformation walkerSourceInformation;

    public DocumentStoreConnectionParseTreeWalker(ParseTreeWalkerSourceInformation walkerSourceInformation)
    {
        this.walkerSourceInformation = walkerSourceInformation;
    }

    public void visitDocumentStoreConnectionValue(DocumentStoreConnectionParser.DefinitionContext ctx, DocumentStoreConnection connectionValue, boolean isEmbedded)
    {
        DocumentStoreConnectionParser.ConnectionStoreContext storeContext = PureGrammarParserUtility.validateAndExtractOptionalField(ctx.connectionStore(), "store", connectionValue.sourceInformation);
        if (storeContext != null)
        {
            connectionValue.element = PureGrammarParserUtility.fromQualifiedName(storeContext.qualifiedName().packagePath() == null ? Collections.emptyList() : storeContext.qualifiedName().packagePath().identifier(), storeContext.qualifiedName().identifier());
            connectionValue.elementSourceInformation = this.walkerSourceInformation.getSourceInformation(storeContext.qualifiedName());
        }
        else if (!isEmbedded)
        {
            PureGrammarParserUtility.validateAndExtractRequiredField(ctx.connectionStore(), "store", connectionValue.sourceInformation);
        }

        DocumentStoreConnectionParser.DbTypeContext dbTypeContext = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.dbType(), "type", connectionValue.sourceInformation);
        try
        {
            connectionValue.type = DatabaseType.valueOf(PureGrammarParserUtility.fromIdentifier(dbTypeContext.identifier()));
            connectionValue.databaseType = DatabaseType.valueOf(PureGrammarParserUtility.fromIdentifier(dbTypeContext.identifier()));
        }
        catch (Exception e)
        {
            throw new EngineException("Unknown database type '" + PureGrammarParserUtility.fromIdentifier(dbTypeContext.identifier()) + "'", this.walkerSourceInformation.getSourceInformation(dbTypeContext), EngineErrorType.PARSER);
        }

        // timezone (optional)
        DocumentStoreConnectionParser.DbConnectionTimezoneContext timezoneCtx = PureGrammarParserUtility.validateAndExtractOptionalField(ctx.dbConnectionTimezone(), "timezone", connectionValue.sourceInformation);
        connectionValue.timeZone = timezoneCtx != null ? timezoneCtx.TIMEZONE().getText() : null;
        // quoteIdentifiers (optional)
        DocumentStoreConnectionParser.DbQuoteIdentifiersContext quoteIdentifiersContext = PureGrammarParserUtility.validateAndExtractOptionalField(ctx.dbQuoteIdentifiers(), "quoteIdentifiers", connectionValue.sourceInformation);
        connectionValue.quoteIdentifiers = quoteIdentifiersContext != null ? Boolean.parseBoolean(quoteIdentifiersContext.BOOLEAN().getText()) : null;
        // datasource specification
        DocumentStoreConnectionParser.NonRelationalDBDatasourceSpecContext dspCtx = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.nonRelationalDBDatasourceSpec(), "specification", connectionValue.sourceInformation);
        connectionValue.datasourceSpecification = this.visitDocumentStoreConnectionDatasourceSpecification(dspCtx);
        // authentication strategy
        DocumentStoreConnectionParser.NonRelationalDBAuthContext authCtx = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.nonRelationalDBAuth(), "auth", connectionValue.sourceInformation);
        connectionValue.authenticationStrategy = this.visitDocumentStoreConnectionAuthenticationStrategy(authCtx);
        //post processors
//        DocumentStoreConnectionParser.RelationalPostProcessorsContext postProcessorsContext = PureGrammarParserUtility.validateAndExtractOptionalField(ctx.relationalPostProcessors(), "postProcessors", connectionValue.sourceInformation);
//        connectionValue.postProcessors = postProcessorsContext != null ? this.visitRelationalPostProcessors(postProcessorsContext) : null;

    }

    private DatasourceSpecification visitDocumentStoreConnectionDatasourceSpecification(DocumentStoreConnectionParser.NonRelationalDBDatasourceSpecContext ctx)
    {
        DocumentStoreConnectionParser.SpecificationContext specificationContext = ctx.specification();
        SourceInformation sourceInformation = walkerSourceInformation.getSourceInformation(ctx);

        DataSourceSpecificationSourceCode code = new DataSourceSpecificationSourceCode(
                ctx.specification().getText(),
                specificationContext.specificationType().getText(),
                sourceInformation,
                ParseTreeWalkerSourceInformation.offset(walkerSourceInformation, ctx.getStart())
        );

        List<IDocumentStoreGrammarParserExtension> extensions = IDocumentStoreGrammarParserExtension.getExtensions();
        DatasourceSpecification ds = IDocumentStoreGrammarParserExtension.process(code, ListIterate.flatCollect(extensions, IDocumentStoreGrammarParserExtension::getExtraDataSourceSpecificationParsers));

        if (ds == null)
        {
            throw new EngineException("Unsupported syntax", this.walkerSourceInformation.getSourceInformation(ctx), EngineErrorType.PARSER);
        }

        return ds;
    }

    public AuthenticationStrategy visitDocumentStoreConnectionAuthenticationStrategy(DocumentStoreConnectionParser.NonRelationalDBAuthContext ctx)
    {
        DocumentStoreConnectionParser.SpecificationContext specification = ctx.specification();
        SourceInformation sourceInformation = walkerSourceInformation.getSourceInformation(ctx);

        AuthenticationStrategySourceCode code = new AuthenticationStrategySourceCode(
                ctx.specification().getText(),
                specification.specificationType().getText(),
                sourceInformation,
                ParseTreeWalkerSourceInformation.offset(walkerSourceInformation, ctx.getStart())
        );

        List<IDocumentStoreGrammarParserExtension> extensions = IDocumentStoreGrammarParserExtension.getExtensions();
        AuthenticationStrategy auth = IDocumentStoreGrammarParserExtension.process(code, ListIterate.flatCollect(extensions, IDocumentStoreGrammarParserExtension::getExtraAuthenticationStrategyParsers));

        if (auth == null)
        {
            throw new EngineException("Unsupported syntax", this.walkerSourceInformation.getSourceInformation(ctx), EngineErrorType.PARSER);
        }

        return auth;
    }
}
