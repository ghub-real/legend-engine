// Copyright 2021 Goldman Sachs
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

package org.finos.legend.engine.language.pure.grammar.from.datasource;

import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserUtility;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.datasource.DataSourceSpecificationParserGrammar;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.StaticDatasourceSpecification;

public class DataSourceSpecificationParseTreeWalker
{
    public MongoDBDatasourceSpecification visitMongoDBDatasourceSpecification(DataSourceSpecificationSourceCode code, DataSourceSpecificationParserGrammar.LocalMongoDatasourceSpecificationContext dbSpecCtx)
    {
        MongoDBDatasourceSpecification dsSpec = new MongoDBDatasourceSpecification();
        dsSpec.sourceInformation = code.getSourceInformation();
        // host
        DataSourceSpecificationParserGrammar.DbHostContext hostCtx = PureGrammarParserUtility.validateAndExtractRequiredField(dbSpecCtx.dbHost(), "host", dsSpec.sourceInformation);
        dsSpec.host = PureGrammarParserUtility.fromGrammarString(hostCtx.STRING().getText(), true);
        // port
        DataSourceSpecificationParserGrammar.DbPortContext portCtx = PureGrammarParserUtility.validateAndExtractRequiredField(dbSpecCtx.dbPort(), "port", dsSpec.sourceInformation);
        dsSpec.port = Integer.parseInt(portCtx.INTEGER().getText());
        // database name
        DataSourceSpecificationParserGrammar.DbNameContext nameCtx = PureGrammarParserUtility.validateAndExtractRequiredField(dbSpecCtx.dbName(), "name", dsSpec.sourceInformation);
        dsSpec.databaseName = PureGrammarParserUtility.fromGrammarString(nameCtx.STRING().getText(), true);
        return dsSpec;
    }

    public StaticDatasourceSpecification visitStaticDatasourceSpecification(DataSourceSpecificationSourceCode code, DataSourceSpecificationParserGrammar.StaticDatasourceSpecificationContext dbSpecCtx)
    {
        StaticDatasourceSpecification dsSpec = new StaticDatasourceSpecification();
        dsSpec.sourceInformation = code.getSourceInformation();
        // host
        DataSourceSpecificationParserGrammar.DbHostContext hostCtx = PureGrammarParserUtility.validateAndExtractRequiredField(dbSpecCtx.dbHost(), "host", dsSpec.sourceInformation);
        dsSpec.host = PureGrammarParserUtility.fromGrammarString(hostCtx.STRING().getText(), true);
        // port
        DataSourceSpecificationParserGrammar.DbPortContext portCtx = PureGrammarParserUtility.validateAndExtractRequiredField(dbSpecCtx.dbPort(), "port", dsSpec.sourceInformation);
        dsSpec.port = Integer.parseInt(portCtx.INTEGER().getText());
        // database name
        DataSourceSpecificationParserGrammar.DbNameContext nameCtx = PureGrammarParserUtility.validateAndExtractRequiredField(dbSpecCtx.dbName(), "name", dsSpec.sourceInformation);
        dsSpec.databaseName = PureGrammarParserUtility.fromGrammarString(nameCtx.STRING().getText(), true);
        return dsSpec;
    }
}
