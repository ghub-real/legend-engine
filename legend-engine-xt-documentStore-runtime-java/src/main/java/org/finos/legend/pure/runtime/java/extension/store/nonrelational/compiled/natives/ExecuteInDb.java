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

package org.finos.legend.pure.runtime.java.extension.store.nonrelational.compiled.natives;

import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_DocumentStoreConnection;
import org.finos.legend.pure.m3.execution.ExecutionSupport;
import org.finos.legend.pure.m4.coreinstance.SourceInformation;
import org.finos.legend.pure.runtime.java.compiled.generation.processors.natives.AbstractNativeFunctionGeneric;

public class ExecuteInDb extends AbstractNativeFunctionGeneric
{
    public ExecuteInDb()
    {
        super("org.finos.legend.pure.runtime.java.extension.store.nonrelational.compiled.NonRelationalGen.executeInDb", new Class[]{String.class, Root_meta_external_store_document_runtime_connections_DocumentStoreConnection.class, long.class, long.class, SourceInformation.class, ExecutionSupport.class},
                true, true, false, "executeInDb_String_1__DocumentStoreConnection_1__Integer_1__Integer_1__ResultSet_1_");
    }
}