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

package org.finos.legend.engine.plan.execution.stores.document.plugin;

import io.opentracing.Scope;
import io.opentracing.util.GlobalTracer;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.utility.Iterate;
import org.finos.legend.engine.plan.execution.nodes.state.ExecutionState;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.plan.execution.stores.StoreType;
import org.finos.legend.engine.plan.execution.stores.document.result.DocumentQueryExecutionResult;
import org.finos.legend.engine.plan.execution.stores.document.result.ResultInterpreterExtension;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.AggregationAwareExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.AllocationExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ConstantExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ErrorExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNodeVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.FreeMarkerConditionalExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.FunctionParametersValidationNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.GraphFetchM2MExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.DocumentQueryExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.MultiResultSequenceExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.PureExpressionPlatformExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.SequenceExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.GlobalGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.GraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.LocalGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryCrossStoreGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryPropertyGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryRootGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.StoreStreamReadingExecutionNode;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class NonRelationalExecutionNodeExecutor implements ExecutionNodeVisitor<Result>
{
    private final ExecutionState executionState;
    private final MutableList<CommonProfile> profiles;
    private MutableList<Function2<ExecutionState, List<Map<String, Object>>, Result>> resultInterpreterExtensions;

    public NonRelationalExecutionNodeExecutor(ExecutionState executionState, MutableList<CommonProfile> profiles)
    {
        this.executionState = executionState;
        this.profiles = profiles;
        this.resultInterpreterExtensions = Iterate.addAllTo(ServiceLoader.load(ResultInterpreterExtension.class), Lists.mutable.empty()).collect(ResultInterpreterExtension::additionalResultBuilder);
    }

    private static Boolean throwNotSupportedException(ExecutionNode node)
    {
        throw new RuntimeException("Unsupported ExecutionNode in Non Relational Execution Node executor - " + node.getClass().getSimpleName());
    }

    @Override
    public Result visit(ExecutionNode executionNode)
    {

        // TODO: goncah convert SQLExecutionNode to MongoNode, for now, until we pass in MongoQueryNode directly.
        if (executionNode instanceof DocumentQueryExecutionNode)
        {

            DocumentQueryExecutionNode documentQueryExecutionNode = (DocumentQueryExecutionNode) executionNode;
            this.executionState.topSpan = GlobalTracer.get().activeSpan();
            try (Scope scope = GlobalTracer.get().buildSpan("Mongo DB Execution").startActive(true))
            {
                scope.span().setTag("databaseType", documentQueryExecutionNode.getDatabaseTypeName());
                scope.span().setTag("mongoQl", documentQueryExecutionNode.mongoQLQuery);
                Result result = ((NonRelationalStoreExecutionState) executionState.getStoreExecutionState(StoreType.NonRelational)).getRelationalExecutor().execute(documentQueryExecutionNode, profiles, executionState);
                if (result instanceof DocumentQueryExecutionResult)
                {
                    scope.span().setTag("executedMongoQL", ((DocumentQueryExecutionResult) result).getExecutedMql());
                }
                return result;
            }

        }
        throw new RuntimeException("Not implemented!");
    }

    @Override
    public Result visit(SequenceExecutionNode sequenceExecutionNode)
    {
        throwNotSupportedException(sequenceExecutionNode);
        return null;
    }

    @Override
    public Result visit(MultiResultSequenceExecutionNode multiResultSequenceExecutionNode)
    {
        throwNotSupportedException(multiResultSequenceExecutionNode);
        return null;
    }

    @Override
    public Result visit(FreeMarkerConditionalExecutionNode localGraphFetchExecutionNode)
    {
        throwNotSupportedException(localGraphFetchExecutionNode);
        return null;
    }

    @Override
    public Result visit(AllocationExecutionNode allocationExecutionNode)
    {
        throwNotSupportedException(allocationExecutionNode);
        return null;
    }

    @Override
    public Result visit(ErrorExecutionNode errorExecutionNode)
    {
        throwNotSupportedException(errorExecutionNode);
        return null;
    }

    @Override
    public Result visit(FunctionParametersValidationNode functionParametersValidationNode)
    {
        throwNotSupportedException(functionParametersValidationNode);
        return null;
    }

    @Override
    public Result visit(GraphFetchExecutionNode graphFetchExecutionNode)
    {
        throwNotSupportedException(graphFetchExecutionNode);
        return null;
    }

    @Override
    public Result visit(GlobalGraphFetchExecutionNode globalGraphFetchExecutionNode)
    {
        throwNotSupportedException(globalGraphFetchExecutionNode);
        return null;
    }

    @Override
    public Result visit(LocalGraphFetchExecutionNode localGraphFetchExecutionNode)
    {
        throwNotSupportedException(localGraphFetchExecutionNode);
        return null;
    }

    @Override
    public Result visit(GraphFetchM2MExecutionNode graphFetchM2MExecutionNode)
    {
        throwNotSupportedException(graphFetchM2MExecutionNode);
        return null;
    }

    @Override
    public Result visit(StoreStreamReadingExecutionNode storeStreamReadingExecutionNode)
    {
        throwNotSupportedException(storeStreamReadingExecutionNode);
        return null;
    }

    @Override
    public Result visit(InMemoryRootGraphFetchExecutionNode inMemoryRootGraphFetchExecutionNode)
    {
        throwNotSupportedException(inMemoryRootGraphFetchExecutionNode);
        return null;
    }

    @Override
    public Result visit(InMemoryCrossStoreGraphFetchExecutionNode inMemoryCrossStoreGraphFetchExecutionNode)
    {
        throwNotSupportedException(inMemoryCrossStoreGraphFetchExecutionNode);
        return null;
    }

    @Override
    public Result visit(InMemoryPropertyGraphFetchExecutionNode inMemoryPropertyGraphFetchExecutionNode)
    {
        throwNotSupportedException(inMemoryPropertyGraphFetchExecutionNode);
        return null;
    }

    @Override
    public Result visit(AggregationAwareExecutionNode aggregationAwareExecutionNode)
    {
        throwNotSupportedException(aggregationAwareExecutionNode);
        return null;
    }

    @Override
    public Result visit(ConstantExecutionNode constantExecutionNode)
    {
        throwNotSupportedException(constantExecutionNode);
        return null;
    }

    @Override
    public Result visit(PureExpressionPlatformExecutionNode pureExpressionPlatformExecutionNode)
    {
        throwNotSupportedException(pureExpressionPlatformExecutionNode);
        return null;
    }
}
