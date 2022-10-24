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

package org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared.clientManager;

import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;
import org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared.ClientWithDataSourceInfo;
import org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared.IClientManagerHandler;
import org.finos.legend.pure.runtime.java.extension.store.relational.shared.connectionManager.ConnectionManager;

public class ClientManagerHandler implements IClientManagerHandler
{
    @Override
    public ClientWithDataSourceInfo getClientWithDataSourceInfo(CoreInstance connectionInformation, ProcessorSupport processorSupport)
    {
        return ClientManager.getClientWithDataSourceInfo(connectionInformation, processorSupport);
    }
}
