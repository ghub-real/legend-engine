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

package org.finos.legend.engine.plan.execution.stores.document.connection.manager.strategic;

import org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.strategy.keys.AuthenticationStrategyKey;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.authentication.strategy.keys.UserNamePasswordAuthenticationStrategyKey;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategyVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.UserNamePasswordAuthenticationStrategy;

public class AuthenticationStrategyKeyGenerator implements AuthenticationStrategyVisitor<AuthenticationStrategyKey>
{
    @Override
    public AuthenticationStrategyKey visit(AuthenticationStrategy authenticationStrategy)
    {
        if (authenticationStrategy instanceof UserNamePasswordAuthenticationStrategy)
        {
            UserNamePasswordAuthenticationStrategy userNamePasswordAuthStrategy = (UserNamePasswordAuthenticationStrategy) authenticationStrategy;
            String userNameVaultReference = userNamePasswordAuthStrategy.baseVaultReference == null ? userNamePasswordAuthStrategy.userNameVaultReference : userNamePasswordAuthStrategy.baseVaultReference + userNamePasswordAuthStrategy.userNameVaultReference;
            String passwordVaultReference = userNamePasswordAuthStrategy.baseVaultReference == null ? userNamePasswordAuthStrategy.passwordVaultReference : userNamePasswordAuthStrategy.baseVaultReference + userNamePasswordAuthStrategy.passwordVaultReference;
            return new UserNamePasswordAuthenticationStrategyKey(userNameVaultReference, passwordVaultReference);
        }
        return null;
    }

}
