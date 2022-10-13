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

package org.finos.legend.engine.plan.execution.stores.relational.plugin;

import org.finos.legend.engine.authentication.provider.DatabaseAuthenticationFlowProvider;
import org.finos.legend.engine.authentication.provider.DatabaseAuthenticationFlowProviderConfiguration;
import org.finos.legend.engine.authentication.provider.DatabaseAuthenticationFlowProviderSelector;
import org.finos.legend.engine.plan.execution.stores.StoreExecutor;
import org.finos.legend.engine.plan.execution.stores.StoreExecutorBuilder;
import org.finos.legend.engine.plan.execution.stores.StoreExecutorConfiguration;
import org.finos.legend.engine.plan.execution.stores.StoreType;
import org.finos.legend.engine.plan.execution.stores.relational.config.NonRelationalExecutionConfiguration;
import org.finos.legend.engine.plan.execution.stores.relational.config.TemporaryTestDbConfiguration;

import java.util.Optional;

public class NonRelationalStoreExecutorBuilder implements StoreExecutorBuilder
{
    private static final int DEFAULT_PORT = -1;
    public static final String DEFAULT_TEMP_PATH = "/tmp/";

    @Override
    public StoreType getStoreType()
    {
        return StoreType.NonRelational;
    }

    @Override
    public NonRelationalStoreExecutor build()
    {
        TemporaryTestDbConfiguration temporaryTestDbConfiguration = new TemporaryTestDbConfiguration(DEFAULT_PORT);
        NonRelationalExecutionConfiguration relationalExecutionConfiguration = new NonRelationalExecutionConfiguration(DEFAULT_TEMP_PATH);
        Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder = this.configureDatabaseAuthenticationFlowProvider(relationalExecutionConfiguration);
        NonRelationalStoreState state = new NonRelationalStoreState(temporaryTestDbConfiguration, relationalExecutionConfiguration, flowProviderHolder);
        return new NonRelationalStoreExecutor(state);
    }

    @Override
    public StoreExecutor build(StoreExecutorConfiguration storeExecutorConfiguration)
    {
        if (!(storeExecutorConfiguration instanceof NonRelationalExecutionConfiguration))
        {
            String message = String.format("Invalid argument. Expected %s but found %s", NonRelationalExecutionConfiguration.class.getCanonicalName(), storeExecutorConfiguration.getClass().getCanonicalName());
            throw new RuntimeException(message);
        }
        NonRelationalExecutionConfiguration relationalExecutionConfiguration = (NonRelationalExecutionConfiguration) storeExecutorConfiguration;
        if (relationalExecutionConfiguration.tempPath == null)
        {
            relationalExecutionConfiguration.tempPath = DEFAULT_TEMP_PATH;
        }
        if (relationalExecutionConfiguration.temporarytestdb == null)
        {
            relationalExecutionConfiguration.temporarytestdb = new TemporaryTestDbConfiguration(DEFAULT_PORT);
        }
        Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder = this.configureDatabaseAuthenticationFlowProvider(relationalExecutionConfiguration);
        NonRelationalStoreState state = new NonRelationalStoreState(relationalExecutionConfiguration.temporarytestdb, relationalExecutionConfiguration, flowProviderHolder);
        return new NonRelationalStoreExecutor(state);
    }

    private Optional<DatabaseAuthenticationFlowProvider> configureDatabaseAuthenticationFlowProvider(NonRelationalExecutionConfiguration nonRelationalExecutionConfig)
    {
        if (nonRelationalExecutionConfig == null)
        {
            return Optional.empty();
        }
        Class<? extends DatabaseAuthenticationFlowProvider> flowProviderClass = nonRelationalExecutionConfig.getFlowProviderClass();
        DatabaseAuthenticationFlowProviderConfiguration flowProviderConfiguration = nonRelationalExecutionConfig.getFlowProviderConfiguration();
        if (flowProviderClass == null)
        {
            // TODO : Implement more strict validation when the flow provider feature is fully rolled out
            return Optional.empty();
        }
        Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder = DatabaseAuthenticationFlowProviderSelector.getProvider(flowProviderClass.getCanonicalName());
        DatabaseAuthenticationFlowProvider flowProvider = flowProviderHolder.orElseThrow(() -> new RuntimeException(String.format("Database authentication provider not found in the classpath. Provider class-%s", flowProviderClass.getCanonicalName())));
        if (flowProviderConfiguration != null)
        {
            flowProvider.configure(flowProviderConfiguration);
        }
        return Optional.of(flowProvider);
    }
}
