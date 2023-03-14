// Copyright 2023 Goldman Sachs
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

package org.finos.legend.engine.protocol.mongodb.schema.metamodel;

import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.engine.protocol.mongodb.schema.metamodel.pure.MongoDBConnection;
import org.finos.legend.engine.protocol.mongodb.schema.metamodel.pure.MongoDBExecutionNode;
import org.finos.legend.engine.protocol.mongodb.schema.metamodel.pure.MongoDBGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.mongodb.schema.metamodel.pure.RootMongoDBGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.extension.ProtocolSubTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.extension.PureProtocolExtension;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.connection.Connection;

import java.util.List;

public class MongoDBPureProtocolExtension implements PureProtocolExtension
{
    @Override
    public List<Function0<List<ProtocolSubTypeInfo<?>>>> getExtraProtocolSubTypeInfoCollectors()
    {
        return Lists.fixedSize.with(() -> Lists.fixedSize.with(
                // Connection
                ProtocolSubTypeInfo.newBuilder(Connection.class)
                        .withSubtype(MongoDBConnection.class, "MongoDBConnection")
                        .build(),
                // Execution Nodes
                ProtocolSubTypeInfo.newBuilder(ExecutionNode.class)
                        .withSubtype(MongoDBExecutionNode.class, "MongoDBExecutionNode")
                        .withSubtype(RootMongoDBGraphFetchExecutionNode.class, "RootMongoDBGraphFetchExecutionNode")
                        .withSubtype(MongoDBGraphFetchExecutionNode.class, "MongoDBGraphFetchExecutionNode")
                        .build()
        ));
    }
}


