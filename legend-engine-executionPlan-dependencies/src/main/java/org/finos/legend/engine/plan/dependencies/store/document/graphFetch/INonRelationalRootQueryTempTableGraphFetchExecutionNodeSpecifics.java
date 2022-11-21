// Copyright 2020 Goldman Sachs
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

package org.finos.legend.engine.plan.dependencies.store.document.graphFetch;

import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.engine.plan.dependencies.domain.graphFetch.IGraphInstance;
import org.finos.legend.engine.plan.dependencies.store.shared.IReferencedObject;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result.DocumentQueryResultField;

import java.lang.reflect.Method;
import java.util.List;

public interface INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics
{
    /* Ordered list of all instance set ids along with respective mapping ids */
    List<Pair<String, String>> allInstanceSetImplementations();

    /* PK columns in result set */
    List<String> primaryKeyColumns(int setIndex);

    /* PK methods for returned objects */
    List<Method> primaryKeyGetters();

    /* Prepare for reading */
    void prepare(List<DocumentQueryResultField> documentQueryResultFields, String databaseTimeZone, String databaseConnection);

    /* Read next graphFetch instance */
    IGraphInstance<? extends IReferencedObject> nextGraphInstance();

    /* If caching specific methods can be called */
    default boolean supportsCaching()
    {
        return false;
    }

    /* Deep copy - required for caching */
    default Object deepCopy(Object object)
    {
        return null;
    }
}