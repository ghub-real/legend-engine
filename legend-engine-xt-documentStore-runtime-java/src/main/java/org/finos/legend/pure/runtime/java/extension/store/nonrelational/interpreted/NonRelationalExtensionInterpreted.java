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

package org.finos.legend.pure.runtime.java.extension.store.nonrelational.interpreted;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.pure.runtime.java.extension.store.nonrelational.interpreted.natives.ExecuteInDb;
import org.finos.legend.pure.runtime.java.interpreted.extension.BaseInterpretedExtension;
import org.finos.legend.pure.runtime.java.interpreted.extension.InterpretedExtension;

public class NonRelationalExtensionInterpreted extends BaseInterpretedExtension
{
    public NonRelationalExtensionInterpreted()
    {
        super(Lists.mutable.with(

                Tuples.pair("executeInDb_String_1__DocumentStoreConnection_1__Integer_1__Integer_1__ResultSet_1_", (e, r) -> new ExecuteInDb(r, e.getMessage(), e.getMaxSQLRows()))

        ));
    }

    public static InterpretedExtension extension()
    {
        return new NonRelationalExtensionInterpreted();
    }
}
