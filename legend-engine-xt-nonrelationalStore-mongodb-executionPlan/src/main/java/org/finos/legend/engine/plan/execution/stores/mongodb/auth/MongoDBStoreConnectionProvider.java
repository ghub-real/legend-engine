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

package org.finos.legend.engine.plan.execution.stores.mongodb.auth;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.authentication.credentialprovider.CredentialProviderProvider;
import org.finos.legend.connection.ConnectionProvider;
import org.finos.legend.connection.ConnectionSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.authentication.specification.AuthenticationSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.authentication.specification.UserPasswordAuthenticationSpecification;
import org.finos.legend.engine.shared.core.identity.Credential;
import org.finos.legend.engine.shared.core.identity.Identity;
import org.finos.legend.engine.shared.core.identity.credential.PlaintextUserPasswordCredential;

import java.util.List;

public class MongoDBStoreConnectionProvider extends ConnectionProvider<MongoClient>
{

    public MongoDBStoreConnectionProvider(CredentialProviderProvider credentialProviderProvider)
    {
        super(credentialProviderProvider);
    }

    @Override
    public MongoClient makeConnection(ConnectionSpecification connectionSpec, AuthenticationSpecification authenticationSpec, Identity identity) throws Exception
    {
        if (!(connectionSpec instanceof MongoDBConnectionSpecification && authenticationSpec instanceof UserPasswordAuthenticationSpecification))
        {
            throw new IllegalStateException("Invalid ConnectionSpecification/AuthenticationSpecification. Please reach out to dev team");
        }

        MongoDBConnectionSpecification mongoDBConnectionSpec = (MongoDBConnectionSpecification) connectionSpec;
        UserPasswordAuthenticationSpecification userPasswordAuthSpec = (UserPasswordAuthenticationSpecification) authenticationSpec;

        MongoClientOptions options = MongoClientOptions.builder().applicationName("Legend Execution Server").build();

        Credential credential = super.makeCredential(userPasswordAuthSpec, identity);

        if (!(credential instanceof PlaintextUserPasswordCredential))
        {
            String message = String.format("Failed to create connected. Expected credential of type %s but found credential of type %s", PlaintextUserPasswordCredential.class, credential.getClass());
            throw new UnsupportedOperationException(message);
        }

        PlaintextUserPasswordCredential plaintextUserPasswordCredential = (PlaintextUserPasswordCredential) credential;

        MongoCredential cred = MongoCredential.createCredential(plaintextUserPasswordCredential.getUser(), mongoDBConnectionSpec.getDatabaseName(),
                plaintextUserPasswordCredential.getPassword().toCharArray());

        List<ServerAddress> serverAddresses = mongoDBConnectionSpec.getServerAddresses();
        MongoClient mongoClient = new MongoClient(serverAddresses, Lists.mutable.of(cred), options);


        return mongoClient;
    }

}