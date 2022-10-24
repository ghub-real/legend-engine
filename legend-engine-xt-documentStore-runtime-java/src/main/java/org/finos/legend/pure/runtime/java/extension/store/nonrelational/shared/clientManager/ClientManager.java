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

public class ClientManager
{
    private ClientManager()
    {
    }

    public static ClientWithDataSourceInfo getClientWithDataSourceInfo(CoreInstance connectionInformation, ProcessorSupport processorSupport)
    {
//        if (processorSupport.instance_instanceOf(connectionInformation, TestDatabaseConnection))
//        {
//            return testDatabaseConnect.getConnectionWithDataSourceInfo(IdentityManager.getAuthenticatedUserId());
//        }
        throw new RuntimeException(connectionInformation + " is not supported for execution!!");
    }

    public static class StatementProperties
    {
        private final String documentQL;
        private final int fetchSize;
        private final int queryTimeoutSeconds;

        private final long startTime = System.currentTimeMillis();

        private StatementProperties(String dql, int fetchSize, int queryTimeoutSeconds)
        {
            this.documentQL = dql;
            this.fetchSize = fetchSize;
            this.queryTimeoutSeconds = queryTimeoutSeconds;
        }

        public String getDocumentQL()
        {
            return this.documentQL;
        }


        public int getFetchSize()
        {
            return this.fetchSize;
        }

        public int getQueryTimeoutSeconds()
        {
            return this.queryTimeoutSeconds;
        }

        public long getStartTime()
        {
            return this.startTime;
        }

        @Override
        public String toString()
        {
            return "StatementProperties{" +
                    "sql='" + this.documentQL.substring(0, 100) + '\'' +
                    ", fetchSize=" + this.fetchSize +
                    ", queryTimeoutSeconds=" + this.queryTimeoutSeconds +
                    ", startTime=" + String.format("%1tY-%<tm-%<td %<tH:%<tM:%<tS.%<tL %<tZ", this.startTime) +
                    '}';
        }
    }
}
