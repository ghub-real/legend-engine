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

package org.finos.legend.engine.language.pure.compiler.toPureGraph;

import org.finos.legend.engine.protocol.pure.v1.model.SourceInformation;
import org.finos.legend.engine.protocol.pure.v1.model.context.EngineErrorType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.DocumentStore;
import org.finos.legend.engine.shared.core.operational.errorManagement.EngineException;
import org.finos.legend.pure.m3.coreinstance.meta.pure.store.Store;

public class HelperDocumentStoreBuilder
{
    private static final String DEFAULT_DOCUMENT_STORE_NAME = "default";

    public static org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore getDocumentStore(String fullPath, SourceInformation sourceInformation, CompileContext context)
    {
        try
        {
            Store store = context.pureModel.getStore(fullPath, sourceInformation);
            if (store instanceof org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore)
            {
                return (org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore) store;
            }
            throw new RuntimeException("Store found but not a Document Store");
        }
        catch (Exception e)
        {
            throw new EngineException("Can't find document store '" + fullPath + "'", sourceInformation, EngineErrorType.COMPILATION);
        }
    }

    public static org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore resolveDocumentStore(String fullPath, SourceInformation sourceInformation, CompileContext context)
    {
        return context.resolve(fullPath, sourceInformation, (String path) -> getDocumentStore(path, sourceInformation, context));
    }
}
