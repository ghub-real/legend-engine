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

import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.plan.execution.nodes.state.ExecutionState;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.plan.execution.stores.StoreExecutionState;
import org.finos.legend.engine.plan.execution.stores.StoreState;
import org.finos.legend.engine.plan.execution.stores.document.NonRelationalExecutor;
import org.finos.legend.engine.plan.execution.stores.document.blockConnection.BlockConnectionContext;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNodeVisitor;
import org.pac4j.core.profile.CommonProfile;

public class NonRelationalStoreExecutionState implements StoreExecutionState
{
    private final NonRelationalStoreState state;
    private boolean retainConnection;
    private BlockConnectionContext blockConnectionContext;
    private RuntimeContext runtimeContext;

    private NonRelationalStoreExecutionState(NonRelationalStoreState storeState, boolean retainConnection, RuntimeContext runtimeContext)
    {
        this.state = storeState;
        this.retainConnection = retainConnection;
        //this.blockConnectionContext = blockConnectionContext;
        this.runtimeContext = runtimeContext;
    }

    public NonRelationalStoreExecutionState(NonRelationalStoreState storeState)
    {
        this(storeState, false, StoreExecutionState.emptyRuntimeContext());  //new BlockConnectionContext(),
    }

    @Override
    public StoreState getStoreState()
    {
        return this.state;
    }

    //    @Override
    //    public ExecutorI getExecutor() {
    //        return null;
    //    }

    @Override
    public ExecutionNodeVisitor<Result> getVisitor(MutableList<CommonProfile> profiles, ExecutionState executionState)
    {
        return new NonRelationalExecutionNodeExecutor(executionState, profiles);
    }

    @Override
    public StoreExecutionState copy()
    {
        // this.retainConnection ? this.blockConnectionContext : this.blockConnectionContext.copy()
        return new NonRelationalStoreExecutionState(this.state, this.retainConnection, this.runtimeContext);
    }

    @Override
    public RuntimeContext getRuntimeContext()
    {
        return this.runtimeContext;
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext)
    {
        this.runtimeContext = runtimeContext;
    }

    public NonRelationalExecutor getNonRelationalExecutor()
    {
        return this.state.getNonRelationalExecutor();
    }

     public boolean retainConnection()
     {
         return this.retainConnection;
     }

        public void setRetainConnection(boolean retainConnection)
        {
            this.retainConnection = retainConnection;
        }

        public BlockConnectionContext getBlockConnectionContext()
        {
            return this.blockConnectionContext;
        }

        public void setBlockConnectionContext(BlockConnectionContext blockConnectionContext)
        {
            this.blockConnectionContext = blockConnectionContext;
        }
}
