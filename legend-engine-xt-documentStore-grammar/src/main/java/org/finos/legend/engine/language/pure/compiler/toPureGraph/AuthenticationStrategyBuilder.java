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

package org.finos.legend.engine.language.pure.compiler.toPureGraph;

import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategyVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.DefaultMongoAuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.TestDatabaseAuthenticationStrategy;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_authentication_AuthenticationStrategy;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_authentication_DefaultMongoAuthenticationStrategy_Impl;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_authentication_TestDatabaseAuthenticationStrategy_Impl;

public class AuthenticationStrategyBuilder implements AuthenticationStrategyVisitor<Root_meta_external_store_document_runtime_authentication_AuthenticationStrategy>
{
    private CompileContext context;

    public AuthenticationStrategyBuilder(CompileContext context)
    {
        this.context = context;
    }

    @Override
    public Root_meta_external_store_document_runtime_authentication_AuthenticationStrategy visit(AuthenticationStrategy authenticationStrategy)
    {
        if (authenticationStrategy instanceof TestDatabaseAuthenticationStrategy)
        {
            return new Root_meta_external_store_document_runtime_authentication_TestDatabaseAuthenticationStrategy_Impl("", null, context.pureModel.getClass("meta::external::store::document::runtime::authentication::TestDatabaseAuthenticationStrategy"));
        }
        if (authenticationStrategy instanceof DefaultMongoAuthenticationStrategy)
        {
            return new Root_meta_external_store_document_runtime_authentication_DefaultMongoAuthenticationStrategy_Impl("", null, context.pureModel.getClass("meta::external::store::document::runtime::authentication::DefaultMongoAuthenticationStrategy"));
        }
        return null;
    }
}