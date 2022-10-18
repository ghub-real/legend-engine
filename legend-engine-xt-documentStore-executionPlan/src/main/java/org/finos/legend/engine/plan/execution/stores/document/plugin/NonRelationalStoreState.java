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

package org.finos.legend.engine.plan.execution.stores.document.plugin;

import org.finos.legend.engine.plan.execution.stores.StoreState;
import org.finos.legend.engine.plan.execution.stores.StoreType;
import org.finos.legend.engine.plan.execution.stores.document.NonRelationalExecutor;
import org.finos.legend.engine.plan.execution.stores.document.config.NonRelationalExecutionConfiguration;
import org.finos.legend.engine.plan.execution.stores.document.config.TemporaryTestDbConfiguration;
import org.finos.legend.engine.plan.execution.stores.nonrelational.client.NonRelationalExecutorInfo;


public class NonRelationalStoreState implements StoreState
{
    private final NonRelationalExecutor nonRelationalExecutor;
    private final NonRelationalExecutorInfo nonRelationalExecutorInfo = new NonRelationalExecutorInfo();

    public NonRelationalStoreState(TemporaryTestDbConfiguration temporarytestdb, NonRelationalExecutionConfiguration relationalExecutionConfiguration)
    {
        this.nonRelationalExecutor = new NonRelationalExecutor(temporarytestdb, relationalExecutionConfiguration);
    }

    public NonRelationalStoreState(int port)
    {
        this(new TemporaryTestDbConfiguration(port), new NonRelationalExecutionConfiguration("/tmp/"));
    }

//    public NonRelationalStoreState(TemporaryTestDbConfiguration temporarytestdb, NonRelationalExecutionConfiguration relationalExecutionConfiguration, Optional<DatabaseAuthenticationFlowProvider> flowProviderHolder)
//    {
//        this.nonRelationalExecutor = new NonRelationalExecutor(temporarytestdb, relationalExecutionConfiguration); // , flowProviderHolder);
//    }

    @Override
    public StoreType getStoreType()
    {
        return StoreType.NonRelational;
    }



    @Override
    public NonRelationalExecutorInfo getStoreExecutionInfo()
    {
        return this.nonRelationalExecutorInfo;
    }

    public NonRelationalExecutor getNonRelationalExecutor()
    {
        return this.nonRelationalExecutor;
    }
}
