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

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.Processor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.connection.Connection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DocumentStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.DocumentStore;
import org.finos.legend.pure.generated.Root_meta_external_store_document_metamodel_DocumentStore_Impl;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_authentication_AuthenticationStrategy;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_DocumentStoreConnection;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_DocumentStoreConnection_Impl;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_specification_DatasourceSpecification;
import org.finos.legend.pure.generated.Root_meta_pure_metamodel_type_generics_GenericType_Impl;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategy;

import java.util.List;

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

    @Override
    public List<Function2<DatasourceSpecification, CompileContext, Root_meta_external_store_document_runtime_connections_specification_DatasourceSpecification>> getExtraDataSourceSpecificationProcessors()
    {
        return org.eclipse.collections.api.factory.Lists.mutable.with((spec, context) ->
        {
            DatasourceSpecificationBuilder datasourceSpecificationVisitor = new DatasourceSpecificationBuilder(context);
            return spec.accept(datasourceSpecificationVisitor);
        });
    }

    @Override
    public List<Function2<AuthenticationStrategy, CompileContext, Root_meta_external_store_document_runtime_authentication_AuthenticationStrategy>> getExtraAuthenticationStrategyProcessors()
    {
        return org.eclipse.collections.api.factory.Lists.mutable.with((strategy, context) ->
        {
            AuthenticationStrategyBuilder authenticationStrategyBuilder = new AuthenticationStrategyBuilder(context);
            return strategy.accept(authenticationStrategyBuilder);
        });
    }

    @Override
    public List<Function2<Connection, CompileContext, org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Connection>> getExtraConnectionValueProcessors()
    {
        return org.eclipse.collections.api.factory.Lists.mutable.with(
                (connectionValue, context) ->
                {
                    if (connectionValue instanceof DocumentStoreConnection)
                    {
                        DocumentStoreConnection documentStoreConnection = (DocumentStoreConnection) connectionValue;

                        Root_meta_external_store_document_runtime_connections_DocumentStoreConnection documentStore = new Root_meta_external_store_document_runtime_connections_DocumentStoreConnection_Impl("", null, context.pureModel.getClass("meta::external::store::document::runtime::connections::DocumentStoreConnection"));
                        HelperDocumentStoreConnectionBuilder.addDocumentStoreConnectionProperties(documentStore, documentStoreConnection.element, documentStoreConnection.elementSourceInformation, documentStoreConnection.type.name(), documentStoreConnection.timeZone, documentStoreConnection.quoteIdentifiers, context);

                        List<IDocumentStoreCompilerExtension> extensions = IDocumentStoreCompilerExtension.getExtensions(context);

                        Root_meta_external_store_document_runtime_authentication_AuthenticationStrategy authenticationStrategy = IDocumentStoreCompilerExtension.process(
                                documentStoreConnection.authenticationStrategy,
                                ListIterate.flatCollect(extensions, IDocumentStoreCompilerExtension::getExtraAuthenticationStrategyProcessors),
                                context);

                        Root_meta_external_store_document_runtime_connections_specification_DatasourceSpecification datasource = IDocumentStoreCompilerExtension.process(
                                documentStoreConnection.datasourceSpecification,
                                ListIterate.flatCollect(extensions, IDocumentStoreCompilerExtension::getExtraDataSourceSpecificationProcessors),
                                context);

//                        List<PostProcessor> postProcessors = documentStoreConnection.postProcessors == null ? FastList.newList() : documentStoreConnection.postProcessors;
//
//                        MutableList<Pair<Root_meta_external_store_document_runtime_connections_PostProcessor, PostProcessorWithParameter>> pp = ListIterate.collect(postProcessors, p -> IDocumentStoreCompilerExtension.process(
//                                p,
//                                ListIterate.flatCollect(extensions, IDocumentStoreCompilerExtension::getExtraConnectionPostProcessor),
//                                context));

                        //we currently need to add both as __queryPostProcessorsWithParameter is used for plan generation
                        //and _postProcessors is used for serialization of plan to protocol
                        documentStore._datasourceSpecification(datasource);
                        documentStore._authenticationStrategy(authenticationStrategy);
//                        List<PostProcessorWithParameter> postProcessorWithParameters = ListIterate.collect(documentStoreConnection.postProcessorWithParameter, p -> IDocumentStoreCompilerExtension.process(
//                                p,
//                                ListIterate.flatCollect(extensions, IRelationalCompilerExtension::getExtraLegacyPostProcessors),
//                                context));
//                        List<PostProcessorWithParameter> translatedForPlanGeneration = ListIterate.collect(pp, Pair::getTwo);
//                        d._queryPostProcessorsWithParameter(org.eclipse.collections.api.factory.Lists.mutable.withAll(postProcessorWithParameters).withAll(translatedForPlanGeneration));
//                        relational._postProcessors(ListIterate.collect(pp, Pair::getOne));

                        return documentStore;
                    }
                    return null;
                }
        );
    }
}
