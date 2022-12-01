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
import org.bson.Document;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.engine.external.shared.format.imports.FileImportContent;
import org.finos.legend.engine.plan.dependencies.domain.dataQuality.BasicChecked;
import org.finos.legend.engine.plan.dependencies.domain.graphFetch.IGraphInstance;
import org.finos.legend.engine.plan.dependencies.store.document.DocumentResultSet;
import org.finos.legend.engine.plan.dependencies.store.document.graphFetch.INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics;
import org.finos.legend.engine.plan.dependencies.store.shared.IReferencedObject;
import org.finos.legend.engine.plan.execution.cache.ExecutionCache;
import org.finos.legend.engine.plan.execution.cache.graphFetch.GraphFetchCacheByEqualityKeys;
import org.finos.legend.engine.plan.execution.cache.graphFetch.GraphFetchCacheKey;
import org.finos.legend.engine.plan.execution.nodes.ExecutionNodeExecutor;
import org.finos.legend.engine.plan.execution.nodes.helpers.platform.ExecutionNodeJavaPlatformHelper;
import org.finos.legend.engine.plan.execution.nodes.state.ExecutionState;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.plan.execution.result.graphFetch.GraphFetchResult;
import org.finos.legend.engine.plan.execution.result.graphFetch.GraphObjectsBatch;
import org.finos.legend.engine.plan.execution.stores.StoreType;
import org.finos.legend.engine.plan.execution.stores.document.NonRelationalDatabaseCommandsVisitorBuilder;
import org.finos.legend.engine.plan.execution.stores.document.result.DocumentQueryExecutionResult;
import org.finos.legend.engine.plan.execution.stores.document.result.PreparedTempTableResult;
import org.finos.legend.engine.plan.execution.stores.document.result.RealizedNonRelationalResult;
import org.finos.legend.engine.plan.execution.stores.document.result.ResultInterpreterExtension;
import org.finos.legend.engine.plan.execution.stores.document.result.graphFetch.NonRelationalGraphObjectsBatch;
//import org.finos.legend.engine.plan.execution.stores.relational.RelationalDatabaseCommandsVisitorBuilder;
import org.finos.legend.engine.plan.execution.stores.document.blockConnection.BlockConnection;
import org.finos.legend.engine.plan.execution.stores.document.blockConnection.BlockConnectionContext;
import org.finos.legend.engine.plan.execution.stores.relational.connection.driver.DatabaseManager;
import org.finos.legend.engine.plan.execution.stores.relational.plugin.RelationalExecutionNodeExecutor;
//import org.finos.legend.engine.plan.execution.stores.relational.plugin.RelationalStoreExecutionState;
//import org.finos.legend.engine.plan.execution.stores.relational.result.FunctionHelper;
//import org.finos.legend.engine.plan.execution.stores.relational.result.PreparedTempTableResult;
import org.finos.legend.engine.plan.execution.stores.document.plugin.tempgenfiles.Specifics;
import org.finos.legend.engine.plan.execution.stores.relational.result.FunctionHelper;
import org.finos.legend.engine.plan.execution.stores.relational.result.RealizedRelationalResult;
import org.finos.legend.engine.plan.execution.stores.relational.result.SQLExecutionResult;
import org.finos.legend.engine.plan.execution.stores.relational.result.graphFetch.RelationalGraphObjectsBatch;
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
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentRootQueryTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.DocumentTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.GlobalGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.GraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.LocalGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.RelationalGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.RelationalTempTableGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryCrossStoreGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryPropertyGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.InMemoryRootGraphFetchExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.StoreStreamReadingExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result.DocumentQueryResultField;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.graph.GraphFetchTree;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.graph.RootGraphFetchTree;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;
import org.finos.legend.engine.shared.core.collectionsExtensions.DoubleStrategyHashMap;
import org.pac4j.core.profile.CommonProfile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NonRelationalExecutionNodeExecutor implements ExecutionNodeVisitor<Result>
{
    private final ExecutionState executionState;

    private FileImportContent fileImportContent;
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
                Result result = ((NonRelationalStoreExecutionState) executionState.getStoreExecutionState(StoreType.NonRelational)).getNonRelationalExecutor().execute(documentQueryExecutionNode, profiles, executionState);
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
            DocumentQueryExecutionResult documentQueryExecutionResult = (DocumentQueryExecutionResult) rootResult;
            DatabaseConnection databaseConnection = documentQueryExecutionResult.getDocumentQueryExecutionNode().connection;
            List<DocumentQueryResultField> resultFields = ((DocumentQueryExecutionResult) rootResult).getDocumentQueryResultFields();



            INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics nodeSpecifics = ExecutionNodeJavaPlatformHelper.getNodeSpecificsInstance(node, this.executionState, this.profiles);

            // goncah copied generated classes for debugging..
//            INonRelationalRootQueryTempTableGraphFetchExecutionNodeSpecifics nodeSpecifics = new Specifics();

            List<Method> primaryKeyGetters = nodeSpecifics.primaryKeyGetters();

            /* Check if caching is enabled and fetch caches if required */
            List<Pair<String, String>> allInstanceSetImplementations = nodeSpecifics.allInstanceSetImplementations();
            int setIdCount = allInstanceSetImplementations.size();
            NonRelationalMultiSetExecutionCacheWrapper multiSetCache = new NonRelationalMultiSetExecutionCacheWrapper(setIdCount);
            boolean cachingEnabledForNode = this.checkForCachingAndPopulateCachingHelpers(allInstanceSetImplementations, nodeSpecifics.supportsCaching(), node.graphFetchTree, documentQueryExecutionResult, nodeSpecifics::primaryKeyColumns, multiSetCache);

            /* Prepare for reading */
            String databaseTimeZone = documentQueryExecutionResult.getDatabaseTimeZone();
           DocumentResultSet documentResultSet = documentQueryExecutionResult.getResultSet();
            String databaseConnectionStr = ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports().writeValueAsString(databaseConnection);
            nodeSpecifics.prepare(documentResultSet, databaseTimeZone, databaseConnectionStr);


//            Object testObj = nodeSpecifics.nextGraphInstance();
            AtomicLong batchIndex = new AtomicLong(0L);



            boolean isUnion = setIdCount > 1;



//
//
//            List<String> resultStrs = Lists.mutable.empty();
//            resultStrs.add("{\n" +
//                    "  \"firstName\" : \"john\",\n" +
//                    "  \"lastName\" : \"smith\"\n" +
//                    "}");
//            GraphObjectsBatch inMemoryGraphObjectsBatch = new GraphObjectsBatch(currentBatch, executionState.getGraphFetchBatchMemoryLimit());
//            inMemoryGraphObjectsBatch.setObjectsForNodeIndex(0, resultStrs);
//
//
//            List<GraphObjectsBatch> objectsBatches = Lists.mutable.empty();
//
//            objectsBatches.add(inMemoryGraphObjectsBatch);
//
//            Stream<GraphObjectsBatch> graphObjectsBatchStream = objectsBatches.stream();


//            return new GraphFetchResult(graphObjectsBatchStream, rootResult).withGraphFetchSpan(graphFetchSpan);




            Spliterator<GraphObjectsBatch> graphObjectsBatchSpliterator = new Spliterators.AbstractSpliterator<GraphObjectsBatch>(Long.MAX_VALUE, Spliterator.ORDERED)
            {
                @Override
                public boolean tryAdvance(Consumer<? super GraphObjectsBatch> action)
                {

                    /* Ensure all children run in the same connection */
                    NonRelationalStoreExecutionState nonRelationalStoreExecutionState = (NonRelationalStoreExecutionState) executionState.getStoreExecutionState(StoreType.NonRelational);
                    //BlockConnectionContext oldBlockConnectionContext = nonRelationalStoreExecutionState.getBlockConnectionContext();
                    boolean oldRetainConnectionFlag = nonRelationalStoreExecutionState.retainConnection();
                    //nonRelationalStoreExecutionState.setBlockConnectionContext(new BlockConnectionContext());
                    nonRelationalStoreExecutionState.setRetainConnection(true);

                    long currentBatch = batchIndex.incrementAndGet();
                    try (Scope ignored = GlobalTracer.get().buildSpan("graph fetch batch").withTag("storeType", "nonRelational").withTag("batchIndex", currentBatch).withTag("class", ((RootGraphFetchTree) node.graphFetchTree)._class).asChildOf(graphFetchSpan).startActive(true))
                    {
                        NonRelationalGraphObjectsBatch relationalGraphObjectsBatch = new NonRelationalGraphObjectsBatch(currentBatch);

                        List<Object> resultObjects = new ArrayList<>();
                        List<Pair<IGraphInstance<? extends IReferencedObject>, ExecutionCache<GraphFetchCacheKey, Object>>> instancesToDeepFetchAndCache = new ArrayList<>();

                        int objectCount = 0;
                            while (documentResultSet.next() )
                        {
                            relationalGraphObjectsBatch.incrementRowCount();

                            int setIndex = 0;
                            Object cachedObject = NonRelationalExecutionNodeExecutor.this.checkAndReturnCachedObject(cachingEnabledForNode, setIndex, multiSetCache);
                            boolean shouldDeepFetchOnThisInstance = cachedObject == null;
                            Object object;
                            if (shouldDeepFetchOnThisInstance)
                            {
                                IGraphInstance<? extends IReferencedObject> wrappedObject = nodeSpecifics.nextGraphInstance();
                                instancesToDeepFetchAndCache.add(Tuples.pair(wrappedObject, multiSetCache.setCaches.get(setIndex)));
                                object = wrappedObject.getValue();
                            }
                            else
                            {
                                object = cachedObject;
                            }
                            if (node.checked != null && node.checked)
                            {
                                resultObjects.add(BasicChecked.newChecked(object, null));
                            }
                            else
                            {
                                resultObjects.add(object);
                            }

                            objectCount += 1;
                            if (objectCount >= batchSize)
                            {
                                break;
                            }
                        }

                        relationalGraphObjectsBatch.setObjectsForNodeIndex(node.nodeIndex, resultObjects);

//                        if (!instancesToDeepFetchAndCache.isEmpty())
//                        {
//                            RealizedNonRelationalResult realizedNonRelationalResult = RealizedNonRelationalResult.emptyRealizedRelationalResult(node.columns);
//                            DoubleStrategyHashMap<Object, Object, DocumentQueryExecutionResult> rootMap = new DoubleStrategyHashMap<>(NonRelationalGraphFetchUtils.objectDocumentQueryResultDoubleHashStrategyWithEmptySecondStrategy(primaryKeyGetters));
//                            for (Pair<IGraphInstance<? extends IReferencedObject>, ExecutionCache<GraphFetchCacheKey, Object>> instanceAndCache : instancesToDeepFetchAndCache)
//                            {
//                                IGraphInstance<? extends IReferencedObject> rootGraphInstance = instanceAndCache.getOne();
//                                Object rootObject = rootGraphInstance.getValue();
//                                rootMap.put(rootObject, rootObject);
//                                relationalGraphObjectsBatch.addObjectMemoryUtilization(rootGraphInstance.instanceSize());
//                                if (!isLeaf)
//                                {
//                                    NonRelationalExecutionNodeExecutor.this.addKeyRowToRealizedNonRelationalResult(rootObject, primaryKeyGetters, realizedNonRelationalResult);
//                                }
//                            }
//
//                            /* Execute store local children */
//                            if (!isLeaf)
//                            {
//                                ExecutionState newState = new ExecutionState(executionState);
//                                newState.graphObjectsBatch = relationalGraphObjectsBatch;
//                                NonRelationalExecutionNodeExecutor.this.executeTempTableNodeChildren(node, realizedNonRelationalResult, databaseConnection, documentQueryExecutionResult.getDatabaseType(), documentQueryExecutionResult.getDatabaseTimeZone(), rootMap, primaryKeyGetters, newState);
//                            }
//                        }
//
//                        instancesToDeepFetchAndCache.stream().filter(x -> x.getTwo() != null).forEach(x ->
//                        {
//                            Object object = x.getOne().getValue();
//                            x.getTwo().put(new NonRelationalGraphFetchUtils.NonRelationalObjectGraphFetchCacheKey(object, primaryKeyGetters), object);
//                        });

                        action.accept(relationalGraphObjectsBatch);

                        return !resultObjects.isEmpty();
                    }
                    catch (/*SQLException |*/ Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                    finally
                    {
                        nonRelationalStoreExecutionState.getBlockConnectionContext().unlockAllBlockConnections();
                        nonRelationalStoreExecutionState.getBlockConnectionContext().closeAllBlockConnectionsAsync();
                        //nonRelationalStoreExecutionState.setBlockConnectionContext(oldBlockConnectionContext);
                        nonRelationalStoreExecutionState.setRetainConnection(oldRetainConnectionFlag);
                    }
                }
            };

            Stream<GraphObjectsBatch> graphObjectsBatchStream = StreamSupport.stream(graphObjectsBatchSpliterator, false);
            return new GraphFetchResult(graphObjectsBatchStream, rootResult).withGraphFetchSpan(graphFetchSpan);
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

    }


    private boolean checkForCachingAndPopulateCachingHelpers(List<Pair<String, String>> allInstanceSetImplementations, boolean nodeSupportsCaching, GraphFetchTree nodeSubTree, DocumentQueryExecutionResult documentQueryExecutionResult, Function<Integer, List<String>> pkColumnsFunction, NonRelationalExecutionNodeExecutor.NonRelationalMultiSetExecutionCacheWrapper multiSetCaches)
    {
        boolean cachingEnabledForNode = (this.executionState.graphFetchCaches != null) && nodeSupportsCaching && NonRelationalGraphFetchUtils.subTreeValidForCaching(nodeSubTree);
        List<DocumentQueryResultField> documentQueryResultFields = documentQueryExecutionResult.getDocumentQueryResultFields();

        if (cachingEnabledForNode)
        {
            int i = 0;
            for (Pair<String, String> setImpl : allInstanceSetImplementations)
            {
                GraphFetchCacheByEqualityKeys cache = NonRelationalGraphFetchUtils.findCacheByEqualityKeys(nodeSubTree, setImpl.getOne(), setImpl.getTwo(), this.executionState.graphFetchCaches);
//                if (cache != null)
//                {
//                    List<Integer> primaryKeyIndices = pkColumnsFunction.apply(i).stream().map(FunctionHelper.unchecked(sqlResultSet::findColumn)).collect(Collectors.toList());
//                    multiSetCaches.addNextValidCache(cache.getExecutionCache(), new NonRelationalGraphFetchUtils.NonRelationalDocumetResultGraphFetchCacheKey(documentQueryExecutionResult, primaryKeyIndices));
//                }
//                else
//                {
//                    multiSetCaches.addNextEmptyCache();
//                }
                i += 1;
            }
        }
        else
        {
            allInstanceSetImplementations.forEach((x) -> multiSetCaches.addNextEmptyCache());
        }

        return cachingEnabledForNode;
    }

    private static class NonRelationalMultiSetExecutionCacheWrapper
    {
        List<Boolean> setCachingEnabled;
        List<ExecutionCache<GraphFetchCacheKey, Object>> setCaches;
        List<NonRelationalGraphFetchUtils.NonRelationalDocumetResultGraphFetchCacheKey> documentResultCacheKeys;

        NonRelationalMultiSetExecutionCacheWrapper(int setIdCount)
        {
            this.setCachingEnabled = new ArrayList<>(setIdCount);
            this.setCaches = new ArrayList<>(setIdCount);
            this.documentResultCacheKeys = new ArrayList<>(setIdCount);
        }

        void addNextValidCache(ExecutionCache<GraphFetchCacheKey, Object> cache, NonRelationalGraphFetchUtils.NonRelationalDocumetResultGraphFetchCacheKey cacheKey)
        {
            this.setCachingEnabled.add(true);
            this.setCaches.add(cache);
            this.documentResultCacheKeys.add(cacheKey);
        }

        void addNextEmptyCache()
        {
            this.setCachingEnabled.add(false);
            this.setCaches.add(null);
            this.documentResultCacheKeys.add(null);
        }
    }

    private void executeTempTableNodeChildren(DocumentTempTableGraphFetchExecutionNode node, RealizedNonRelationalResult realizedNonRelationalResult, org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection databaseConnection, String databaseType, String databaseTimeZone, DoubleStrategyHashMap<Object, Object, DocumentQueryExecutionResult> nodeObjectsMap, List<Method> nodePrimaryKeyGetters, ExecutionState state)
    {
        NonRelationalGraphObjectsBatch nonRelationalGraphObjectsBatch = (NonRelationalGraphObjectsBatch) state.graphObjectsBatch;

        if (realizedNonRelationalResult.resultSetRows.isEmpty())
        {
            node.children.forEach(x -> this.recursivelyPopulateEmptyResultsInGraphObjectsBatch(x, nonRelationalGraphObjectsBatch));
        }
        else
        {
            String tempTableName = DatabaseManager.fromString(databaseType).relationalDatabaseSupport().processTempTableName(node.tempTableName);
            NonRelationalExecutionNodeExecutor.this.createTempTableFromRealizedRelationalResultInBlockConnection(realizedNonRelationalResult, tempTableName, databaseConnection, databaseType, databaseTimeZone);
            state.addResult(node.tempTableName, new PreparedTempTableResult(tempTableName));

            nonRelationalGraphObjectsBatch.setNodeObjectsHashMap(node.nodeIndex, nodeObjectsMap);
            nonRelationalGraphObjectsBatch.setNodePrimaryKeyGetters(node.nodeIndex, nodePrimaryKeyGetters);

            node.children.forEach(x -> x.accept(new ExecutionNodeExecutor(this.profiles, state)));
        }
    }

    private void recursivelyPopulateEmptyResultsInGraphObjectsBatch(DocumentGraphFetchExecutionNode node, NonRelationalGraphObjectsBatch nonRelationalGraphObjectsBatch)
    {
        nonRelationalGraphObjectsBatch.setObjectsForNodeIndex(node.nodeIndex, Collections.emptyList());
        if (node.children != null && !node.children.isEmpty())
        {
            node.children.forEach(x -> this.recursivelyPopulateEmptyResultsInGraphObjectsBatch(x, nonRelationalGraphObjectsBatch));
        }
    }

    private void createTempTableFromRealizedRelationalResultInBlockConnection(RealizedNonRelationalResult realizedNonRelationalResult, String tempTableName, org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection databaseConnection, String databaseType, String databaseTimeZone)
    {
        // this isn't valid anymore
        try (Scope ignored = GlobalTracer.get().buildSpan("create temp table").withTag("tempTableName", tempTableName).withTag("databaseType", databaseType).startActive(true))
        {
            NonRelationalStoreExecutionState nonRelationalStoreExecutionState = (NonRelationalStoreExecutionState) this.executionState.getStoreExecutionState(StoreType.NonRelational);
            DatabaseManager databaseManager = DatabaseManager.fromString(databaseType);
            BlockConnection blockConnection = nonRelationalStoreExecutionState.getBlockConnectionContext().getBlockConnection(nonRelationalStoreExecutionState, databaseConnection, this.profiles);
            databaseManager.relationalDatabaseSupport().accept(NonRelationalDatabaseCommandsVisitorBuilder.getStreamResultToTempTableVisitor(nonRelationalStoreExecutionState.getNonRelationalExecutor().getNonRelationalExecutionConfiguration(), blockConnection, realizedNonRelationalResult, tempTableName, databaseTimeZone));
            blockConnection.addCommitQuery(databaseManager.relationalDatabaseSupport().dropTempTable(tempTableName));
            blockConnection.addRollbackQuery(databaseManager.relationalDatabaseSupport().dropTempTable(tempTableName));
            blockConnection.close();
        }
    }

    private void addKeyRowToRealizedNonRelationalResult(Object obj, List<Method> keyGetters, RealizedNonRelationalResult realizedNonRelationalResult) throws InvocationTargetException, IllegalAccessException
    {
        int keyCount = keyGetters.size();
        List<Object> pkRowTransformed = FastList.newList(keyCount);
        List<Object> pkRowNormalized = FastList.newList(keyCount);

        for (Method keyGetter : keyGetters)
        {
            Object key = keyGetter.invoke(obj);
            pkRowTransformed.add(key);
            pkRowNormalized.add(key);
        }

        realizedNonRelationalResult.addRow(pkRowNormalized, pkRowTransformed);
    }

    private Object checkAndReturnCachedObject(boolean cachingEnabledForNode, int setIndex, NonRelationalExecutionNodeExecutor.NonRelationalMultiSetExecutionCacheWrapper multiSetCache)
    {
        if (cachingEnabledForNode && multiSetCache.setCachingEnabled.get(setIndex))
        {
            return multiSetCache.setCaches.get(setIndex).getIfPresent(multiSetCache.documentResultCacheKeys.get(setIndex));
        }
        return null;
    }
}
