// Copyright 2022 Goldman Sachs
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

package org.finos.legend.engine.protocol.pure.v1;

import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.finos.legend.engine.protocol.pure.v1.extension.ProtocolSubTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.extension.PureProtocolExtension;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.DocumentQueryExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentClassQueryTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentCrossRootGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentCrossRootQueryTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentPrimitiveQueryGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentRootGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentRootQueryTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.connection.Connection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.mapping.ClassMapping;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DataStoreConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.AuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.TestDatabaseAuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.authentication.UserNamePasswordAuthenticationStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.mapping.NonRelationalClassMapping;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.DocumentStore;

import java.util.List;
import java.util.Map;

public class DocumentStoreProtocolExtension  implements PureProtocolExtension
{
    @Override
    public List<Function0<List<ProtocolSubTypeInfo<?>>>> getExtraProtocolSubTypeInfoCollectors()
    {
        //return Lists.fixedSize.empty();
        return Lists.fixedSize.with(() -> Lists.fixedSize.with(

//        return Lists.fixedSize.with(() -> Lists.fixedSize.with(
                // Packageable element
                ProtocolSubTypeInfo.newBuilder(PackageableElement.class)
                        .withSubtype(DocumentStore.class, "nonRelational")
                        .build(),
//                // Value specification
//                ProtocolSubTypeInfo.newBuilder(ValueSpecification.class)
//                        .withSubtype(DatabaseInstance.class, "databaseInstance")
//                        .build(),
                // Class mapping
                ProtocolSubTypeInfo.newBuilder(ClassMapping.class)
                        .withSubtype(NonRelationalClassMapping.class, "nonRelational")
                        .build(),
//                // Mapping Test InputData
//                ProtocolSubTypeInfo.newBuilder(InputData.class)
//                        .withSubtype(RelationalInputData.class, "relational")
//                        .build(),
//                // Association mapping
//                ProtocolSubTypeInfo.newBuilder(AssociationMapping.class)
//                        .withSubtype(RelationalAssociationMapping.class, "relational")
//                        .build(),
//                // Property mapping
//                ProtocolSubTypeInfo.newBuilder(PropertyMapping.class)
//                        .withSubtype(RelationalPropertyMapping.class, "relationalPropertyMapping")
//                        .withSubtype(EmbeddedRelationalPropertyMapping.class, "embeddedPropertyMapping")
//                        .withSubtype(InlineEmbeddedPropertyMapping.class, "inlineEmbeddedPropertyMapping")
//                        .withSubtype(OtherwiseEmbeddedRelationalPropertyMapping.class, "otherwiseEmbeddedPropertyMapping")
//                        .build(),
                // Connection
                ProtocolSubTypeInfo.newBuilder(Connection.class)
                        .withSubtype(DataStoreConnection.class, "DocumentStoreConnection")
                        .build(),
//                // Execution context
//                ProtocolSubTypeInfo.newBuilder(ExecutionContext.class)
//                        .withSubtype(RelationalExecutionContext.class, "RelationalExecutionContext")
//                        .build(),
//                // Execution plan result type
//                ProtocolSubTypeInfo.newBuilder(ResultType.class)
//                        .withSubtype(RelationResultType.class, "relation")
//                        .build(),
//                // Embedded Data
//                ProtocolSubTypeInfo.newBuilder(EmbeddedData.class)
//                        .withSubtype(RelationalCSVData.class, "relationalCSVData")
//                        .build(),
                // Execution plan node
                ProtocolSubTypeInfo.newBuilder(ExecutionNode.class)
                        .withSubtype(DocumentQueryExecutionNode.class, "documentQuery")
                        //.withSubtype(RelationalTdsInstantiationExecutionNode.class, "relationalTdsInstantiation")
                        //.withSubtype(RelationalClassInstantiationExecutionNode.class, "relationalClassInstantiation")
                        //.withSubtype(RelationalRelationDataInstantiationExecutionNode.class, "relationalRelationDataInstantiation")
                        //.withSubtype(RelationalDataTypeInstantiationExecutionNode.class, "relationalDataTypeInstantiation")
                        .withSubtype(DocumentRootGraphFetchExecutionNode.class, "documentRootGraphFetchExecutionNode")
                        .withSubtype(DocumentCrossRootGraphFetchExecutionNode.class, "documentCrossRootGraphFetchExecutionNode")
                        .withSubtype(DocumentTempTableGraphFetchExecutionNode.class, "documentTempTableGraphFetchExecutionNode")
                        .withSubtype(DocumentGraphFetchExecutionNode.class, "documentGraphFetchExecutionNode")
                        //.withSubtype(RelationalBlockExecutionNode.class, "relationalBlock")
                        //.withSubtype(CreateAndPopulateTempTableExecutionNode.class, "createAndPopulateTempTable")
                        .withSubtype(DocumentPrimitiveQueryGraphFetchExecutionNode.class, "documentPrimitiveQueryGraphFetch")
                        .withSubtype(DocumentClassQueryTempTableGraphFetchExecutionNode.class, "documentClassQueryTempTableGraphFetch")
                        .withSubtype(DocumentRootQueryTempTableGraphFetchExecutionNode.class, "documentRootQueryTempTableGraphFetch")
                        .withSubtype(DocumentCrossRootQueryTempTableGraphFetchExecutionNode.class, "documentCrossRootQueryTempTableGraphFetch")
                        .build(),

                //DatasourceSpecification
                ProtocolSubTypeInfo.newBuilder(DatasourceSpecification.class)
                        .withSubtype(MongoDBDatasourceSpecification.class, "mongoDB")
                        .build(),

                // AuthenticationStrategy
                ProtocolSubTypeInfo.newBuilder(AuthenticationStrategy.class)
                        .withSubtype(TestDatabaseAuthenticationStrategy.class, "test")
                        .withSubtype(UserNamePasswordAuthenticationStrategy.class, "userNamePassword")
                        .build()

//                //Post Processor
//                ProtocolSubTypeInfo.newBuilder(PostProcessor.class)
//                        .withSubtype(MapperPostProcessor.class, "mapper")
//                        .build(),
//
//                //Post Processor Parameter
//                ProtocolSubTypeInfo.newBuilder(Milestoning.class)
//                        .withSubtype(BusinessMilestoning.class, "businessMilestoning")
//                        .withSubtype(BusinessSnapshotMilestoning.class, "businessSnapshotMilestoning")
//                        .withSubtype(ProcessingMilestoning.class, "processingMilestoning")
//                        .build()
                        ));
    }

    @Override
    public Map<Class<? extends PackageableElement>, String> getExtraProtocolToClassifierPathMap()
    {
        return Maps.mutable.with(DocumentStore.class, "meta::external::store::document::metamodel::DocumentStore");
    }
}
