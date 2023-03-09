package org.finos.legend.engine.plan.execution.stores.mongodb.config;

import org.finos.legend.authentication.credentialprovider.CredentialProviderProvider;
import org.finos.legend.engine.plan.execution.stores.StoreExecutorConfiguration;
import org.finos.legend.engine.plan.execution.stores.StoreType;

public class MongoDBExecutionConfiguration implements StoreExecutorConfiguration
{
    private CredentialProviderProvider credentialProviderProvider;

    @Override
    public StoreType getStoreType()
    {
        return StoreType.NonRelational_MongoDB;
    }

    public CredentialProviderProvider getCredentialProviderProvider()
    {
        return credentialProviderProvider;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private CredentialProviderProvider credentialProviderProvider = CredentialProviderProvider.builder().build();

        public Builder withCredentialProviderProvider(CredentialProviderProvider credentialProviderProvider)
        {
            this.credentialProviderProvider = credentialProviderProvider;
            return this;
        }

        public MongoDBExecutionConfiguration build()
        {
            MongoDBExecutionConfiguration mongoDBExecutionConfiguration = new MongoDBExecutionConfiguration();
            mongoDBExecutionConfiguration.credentialProviderProvider = credentialProviderProvider;
            return mongoDBExecutionConfiguration;
        }
    }
}
