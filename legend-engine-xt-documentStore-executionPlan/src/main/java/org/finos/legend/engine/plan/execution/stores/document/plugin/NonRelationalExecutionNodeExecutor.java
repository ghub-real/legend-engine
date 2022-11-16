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
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.utility.Iterate;
import org.finos.legend.engine.plan.execution.nodes.ExecutionNodeExecutor;
import org.finos.legend.engine.plan.execution.nodes.state.ExecutionState;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.plan.execution.stores.StoreType;
import org.finos.legend.engine.plan.execution.stores.document.result.DocumentQueryExecutionResult;
import org.finos.legend.engine.plan.execution.stores.document.result.ResultInterpreterExtension;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.AggregationAwareExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.AllocationExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ConstantExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.DocumentQueryExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ErrorExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNodeVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.FreeMarkerConditionalExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.FunctionParametersValidationNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.GraphFetchM2MExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.MultiResultSequenceExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.PureExpressionPlatformExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.SequenceExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentRootQueryTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.GlobalGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.GraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.LocalGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryCrossStoreGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryPropertyGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryRootGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.StoreStreamReadingExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result.DocumentQueryResultField;
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
        else if (executionNode instanceof DocumentRootQueryTempTableGraphFetchExecutionNode)
        {
            return executeDocumentRootQueryTempTableGraphFetchExecutionNode((DocumentRootQueryTempTableGraphFetchExecutionNode) executionNode);
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


    private Result executeDocumentRootQueryTempTableGraphFetchExecutionNode(DocumentRootQueryTempTableGraphFetchExecutionNode node)
    {
        int batchSize = node.batchSize == null ? 1000 : node.batchSize;
        boolean isLeaf = node.children == null || node.children.isEmpty();
        Result rootResult = null;

        Span graphFetchSpan = GlobalTracer.get().buildSpan("graph fetch").withTag("rootStoreType", "relational").withTag("batchSizeConfig", batchSize).start();
        GlobalTracer.get().activateSpan(graphFetchSpan);
        try
        {
            rootResult = node.executionNodes.get(0).accept(new ExecutionNodeExecutor(this.profiles, this.executionState));
            DocumentQueryExecutionResult executionResult = (DocumentQueryExecutionResult) rootResult;
            DatabaseConnection databaseConnection = executionResult.getDocumentQueryExecutionNode().connection;
            List<DocumentQueryResultField> resultFields = ((DocumentQueryExecutionResult) rootResult).getDocumentQueryResultFields();

//            IRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics nodeSpecifics = ExecutionNodeJavaPlatformHelper.getNodeSpecificsInstance(node, this.executionState, this.profiles);

//            List<Method> primaryKeyGetters = nodeSpecifics.primaryKeyGetters();
//
//            /* Check if caching is enabled and fetch caches if required */
//            List<Pair<String, String>> allInstanceSetImplementations = nodeSpecifics.allInstanceSetImplementations();
//            int setIdCount = allInstanceSetImplementations.size();
//            RelationalExecutionNodeExecutor.RelationalMultiSetExecutionCacheWrapper multiSetCache = new RelationalExecutionNodeExecutor.RelationalMultiSetExecutionCacheWrapper(setIdCount);
//            boolean cachingEnabledForNode = this.checkForCachingAndPopulateCachingHelpers(allInstanceSetImplementations, nodeSpecifics.supportsCaching(), node.graphFetchTree, sqlExecutionResult, nodeSpecifics::primaryKeyColumns, multiSetCache);
//
//            /* Prepare for reading */
//            nodeSpecifics.prepare(rootResultSet, sqlExecutionResult.getDatabaseTimeZone(), ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports().writeValueAsString(databaseConnection));
//
//            boolean isUnion = setIdCount > 1;
//            AtomicLong batchIndex = new AtomicLong(0L);
//            Spliterator<GraphObjectsBatch> graphObjectsBatchSpliterator = new Spliterators.AbstractSpliterator<GraphObjectsBatch>(Long.MAX_VALUE, Spliterator.ORDERED)
//            {
//                @Override
//                public boolean tryAdvance(Consumer<? super GraphObjectsBatch> action)
//                {
//
//                    /* Ensure all children run in the same connection */
//                    RelationalStoreExecutionState relationalStoreExecutionState = (RelationalStoreExecutionState) executionState.getStoreExecutionState(StoreType.Relational);
//                    BlockConnectionContext oldBlockConnectionContext = relationalStoreExecutionState.getBlockConnectionContext();
//                    boolean oldRetainConnectionFlag = relationalStoreExecutionState.retainConnection();
//                    relationalStoreExecutionState.setBlockConnectionContext(new BlockConnectionContext());
//                    relationalStoreExecutionState.setRetainConnection(true);
//
//                    long currentBatch = batchIndex.incrementAndGet();
//                    try (Scope ignored = GlobalTracer.get().buildSpan("graph fetch batch").withTag("storeType", "relational").withTag("batchIndex", currentBatch).withTag("class", ((RootGraphFetchTree) node.graphFetchTree)._class).asChildOf(graphFetchSpan).startActive(true))
//                    {
//                        RelationalGraphObjectsBatch relationalGraphObjectsBatch = new RelationalGraphObjectsBatch(currentBatch);
//
//                        List<Object> resultObjects = new ArrayList<>();
//                        List<Pair<IGraphInstance<? extends IReferencedObject>, ExecutionCache<GraphFetchCacheKey, Object>>> instancesToDeepFetchAndCache = new ArrayList<>();
//
//                        int objectCount = 0;
//                        while ((!rootResultSet.isClosed()) && rootResultSet.next())
//                        {
//                            relationalGraphObjectsBatch.incrementRowCount();
//
//                            int setIndex = isUnion ? rootResultSet.getInt(1) : 0;
//                            Object cachedObject = RelationalExecutionNodeExecutor.this.checkAndReturnCachedObject(cachingEnabledForNode, setIndex, multiSetCache);
//                            boolean shouldDeepFetchOnThisInstance = cachedObject == null;
//                            Object object;
//                            if (shouldDeepFetchOnThisInstance)
//                            {
//                                IGraphInstance<? extends IReferencedObject> wrappedObject = nodeSpecifics.nextGraphInstance();
//                                instancesToDeepFetchAndCache.add(Tuples.pair(wrappedObject, multiSetCache.setCaches.get(setIndex)));
//                                object = wrappedObject.getValue();
//                            }
//                            else
//                            {
//                                object = cachedObject;
//                            }
//                            if (node.checked != null && node.checked)
//                            {
//                                resultObjects.add(BasicChecked.newChecked(object, null));
//                            }
//                            else
//                            {
//                                resultObjects.add(object);
//                            }
//
//                            objectCount += 1;
//                            if (objectCount >= batchSize)
//                            {
//                                break;
//                            }
//                        }
//
//                        relationalGraphObjectsBatch.setObjectsForNodeIndex(node.nodeIndex, resultObjects);
//
//                        if (!instancesToDeepFetchAndCache.isEmpty())
//                        {
//                            RealizedRelationalResult realizedRelationalResult = RealizedRelationalResult.emptyRealizedRelationalResult(node.columns);
//                            DoubleStrategyHashMap<Object, Object, SQLExecutionResult> rootMap = new DoubleStrategyHashMap<>(RelationalGraphFetchUtils.objectSQLResultDoubleHashStrategyWithEmptySecondStrategy(primaryKeyGetters));
//                            for (Pair<IGraphInstance<? extends IReferencedObject>, ExecutionCache<GraphFetchCacheKey, Object>> instanceAndCache : instancesToDeepFetchAndCache)
//                            {
//                                IGraphInstance<? extends IReferencedObject> rootGraphInstance = instanceAndCache.getOne();
//                                Object rootObject = rootGraphInstance.getValue();
//                                rootMap.put(rootObject, rootObject);
//                                relationalGraphObjectsBatch.addObjectMemoryUtilization(rootGraphInstance.instanceSize());
//                                if (!isLeaf)
//                                {
//                                    RelationalExecutionNodeExecutor.this.addKeyRowToRealizedRelationalResult(rootObject, primaryKeyGetters, realizedRelationalResult);
//                                }
//                            }
//
//                            /* Execute store local children */
//                            if (!isLeaf)
//                            {
//                                ExecutionState newState = new ExecutionState(executionState);
//                                newState.graphObjectsBatch = relationalGraphObjectsBatch;
//                                RelationalExecutionNodeExecutor.this.executeTempTableNodeChildren(node, realizedRelationalResult, databaseConnection, sqlExecutionResult.getDatabaseType(), sqlExecutionResult.getDatabaseTimeZone(), rootMap, primaryKeyGetters, newState);
//                            }
//                        }
//
//                        instancesToDeepFetchAndCache.stream().filter(x -> x.getTwo() != null).forEach(x ->
//                        {
//                            Object object = x.getOne().getValue();
//                            x.getTwo().put(new RelationalGraphFetchUtils.RelationalObjectGraphFetchCacheKey(object, primaryKeyGetters), object);
//                        });
//
//                        action.accept(relationalGraphObjectsBatch);
//
//                        return !resultObjects.isEmpty();
//                    }
//                    catch (SQLException | InvocationTargetException | IllegalAccessException e)
//                    {
//                        throw new RuntimeException(e);
//                    }
//                    finally
//                    {
//                        relationalStoreExecutionState.getBlockConnectionContext().unlockAllBlockConnections();
//                        relationalStoreExecutionState.getBlockConnectionContext().closeAllBlockConnectionsAsync();
//                        relationalStoreExecutionState.setBlockConnectionContext(oldBlockConnectionContext);
//                        relationalStoreExecutionState.setRetainConnection(oldRetainConnectionFlag);
//                    }
//                }
//            };
//
//            Stream<GraphObjectsBatch> graphObjectsBatchStream = StreamSupport.stream(graphObjectsBatchSpliterator, false);
//            return new GraphFetchResult(graphObjectsBatchStream, rootResult).withGraphFetchSpan(graphFetchSpan);
        }
        catch (RuntimeException e)
        {
            if (rootResult != null)
            {
                rootResult.close();
            }
            if (graphFetchSpan != null)
            {
                graphFetchSpan.finish();
            }
            throw e;
        }
        catch (Exception e)
        {
            if (rootResult != null)
            {
                rootResult.close();
            }
            if (graphFetchSpan != null)
            {
                graphFetchSpan.finish();
            }
            throw new RuntimeException(e);
        }

        return rootResult;
    }
}