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

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.Processor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.DocumentStore;
import org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore_Impl;
import org.finos.legend.pure.generated.Root_meta_pure_metamodel_type_generics_GenericType_Impl;

public class DocumentStoreCompilerExtension implements IDocumentStoreCompilerExtension
{
    @Override
    public Iterable<? extends Processor<?>> getExtraProcessors()
    {
        return Lists.immutable.with(Processor.newProcessor(
                DocumentStore.class,
                (DocumentStore srcDocumentStore, CompileContext context) ->
                {
                    org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore documentStore = new Root_meta_external_store_document_metamodel_DocumentStore_Impl(srcDocumentStore.name)._name(srcDocumentStore.name);

                    documentStore._classifierGenericType(new Root_meta_pure_metamodel_type_generics_GenericType_Impl("", null, context.pureModel.getClass("meta::pure::metamodel::type::generics::GenericType"))
                            ._rawType(context.pureModel.getType("meta::external::store::document::metamodel::DocumentStore")));

                    context.pureModel.storesIndex.put(context.pureModel.buildPackageString(srcDocumentStore._package, srcDocumentStore.name), documentStore);
                    return documentStore;
                },
                (DocumentStore srcDocumentStore, CompileContext context) ->
                {
                    org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore documentStore = HelperDocumentStoreBuilder.getDocumentStore(context.pureModel.buildPackageString(srcDocumentStore._package, srcDocumentStore.name), srcDocumentStore.sourceInformation, context);
                    if (!srcDocumentStore.includedStores.isEmpty())
                    {
                        documentStore._includes(ListIterate.collect(srcDocumentStore.includedStores, include -> HelperDocumentStoreBuilder.resolveDocumentStore(context.pureModel.addPrefixToTypeReference(include), srcDocumentStore.sourceInformation, context)));
                    }
                }
        ));
    }
}
