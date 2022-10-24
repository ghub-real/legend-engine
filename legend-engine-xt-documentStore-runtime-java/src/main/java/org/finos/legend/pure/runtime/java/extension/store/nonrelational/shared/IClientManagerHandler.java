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

package org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;
import org.finos.legend.pure.runtime.java.extension.store.nonrelational.shared.clientManager.ClientManagerHandler;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface IClientManagerHandler
{
    ImmutableList<IClientManagerHandler> CLIENT_MANAGER_HANDLERS = Lists.immutable.withAll(ServiceLoader.load(IClientManagerHandler.class));
    IClientManagerHandler CLIENT_MANAGER_HANDLER = getHandler();

    static IClientManagerHandler getHandler()
    {
        if (CLIENT_MANAGER_HANDLERS.size() == 1)
        {
            return CLIENT_MANAGER_HANDLERS.get(0);
        }
        if (CLIENT_MANAGER_HANDLERS.isEmpty())
        {
            return new ClientManagerHandler();
        }
        throw new RuntimeException("Multiple ConnectionManagerHandler present in scope - " + CLIENT_MANAGER_HANDLERS.stream().map(handler -> handler.getClass().getName()).collect(Collectors.joining(",", "[", "]")));
    }

    ClientWithDataSourceInfo getClientWithDataSourceInfo(CoreInstance connectionInformation, ProcessorSupport processorSupport);

}
