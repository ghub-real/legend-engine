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

package org.finos.legend.engine.language.pure.grammar.from.authentication;

import org.finos.legend.engine.language.pure.grammar.from.antlr4.connection.authentication.AuthenticationStrategyParserGrammar;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.DefaultMongoAuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.TestDatabaseAuthenticationStrategy;

public class AuthenticationStrategyParseTreeWalker
{
    public DefaultMongoAuthenticationStrategy visitDefaultMongoAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.DefaultMongoAuthContext authCtx)
    {
        DefaultMongoAuthenticationStrategy authStrategy = new DefaultMongoAuthenticationStrategy();
        authStrategy.sourceInformation = code.getSourceInformation();
        return authStrategy;
    }

    public TestDatabaseAuthenticationStrategy visitTestDatabaseAuthenticationStrategy(AuthenticationStrategySourceCode code, AuthenticationStrategyParserGrammar.TestDBAuthContext authCtx)
    {
        TestDatabaseAuthenticationStrategy authStrategy = new TestDatabaseAuthenticationStrategy();
        authStrategy.sourceInformation = code.getSourceInformation();
        return authStrategy;
    }
}