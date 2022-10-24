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

import org.finos.legend.pure.m3.navigation.Instance;
import org.finos.legend.pure.m3.navigation.M3Properties;
import org.finos.legend.pure.m3.navigation.PrimitiveUtilities;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;

import java.util.Objects;

public class DataSource
{
    private final String host;
    private final Integer port;
    private final String dataSourceName;
    private final String serverPrincipal;

    //private final DriverConfig driverConfig;

    public DataSource(String host, Integer port, String dataSourceName, String serverPrincipal)
    {
        this.host = host;
        this.port = port;
        this.dataSourceName = dataSourceName;
        this.serverPrincipal = serverPrincipal;
    }

    public String getHost()
    {
        return host;
    }

    public Integer getPort()
    {
        return port;
    }

    public String getDataSourceName()
    {
        return dataSourceName;
    }

    public String getServerPrincipal()
    {
        return serverPrincipal;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSource that = (DataSource) o;
        return Objects.equals(host, that.host) && Objects.equals(port, that.port) && Objects.equals(dataSourceName, that.dataSourceName) && Objects.equals(serverPrincipal, that.serverPrincipal);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(host, port, dataSourceName, serverPrincipal);
    }

    public static DataSource newDataSource(CoreInstance dataSource, ProcessorSupport processorSupport)
    {
        String host = Instance.getValueForMetaPropertyToOneResolved(dataSource, "host", processorSupport).getName();
        Number port = PrimitiveUtilities.getIntegerValue(Instance.getValueForMetaPropertyToOneResolved(dataSource, "port", processorSupport));
        String dataSourceName = Instance.getValueForMetaPropertyToOneResolved(dataSource, M3Properties.name, processorSupport).getName();
        String serverPrincipal = PrimitiveUtilities.getStringValue(Instance.getValueForMetaPropertyToOneResolved(dataSource, "serverPrincipal", processorSupport), null);

        return new DataSource(host, (Integer)port, dataSourceName, serverPrincipal);
    }

    public static DataSource newDataSource(String host, int port, String dataSourceName)
    {
        return new DataSource(host, port, dataSourceName, null);
    }
}
