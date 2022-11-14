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

package org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.ExecutionNodeVisitor;

import java.util.List;

public class DocumentGraphFetchExecutionNode extends LocalGraphFetchExecutionNode
{
    @Deprecated
    public ExecutionNode nonRelationalNode;
    public List<DocumentGraphFetchExecutionNode> children;

    @Override
    public <T> T accept(ExecutionNodeVisitor<T> executionNodeVisitor)
    {
        return executionNodeVisitor.visit((ExecutionNode) this);
    }

    @Override
    @JsonIgnore
    public List<ExecutionNode> childNodes()
    {
        List<ExecutionNode> allNodes = this.executionNodes().toList();

        if (this.nonRelationalNode != null)
        {
            allNodes.add(this.nonRelationalNode);
        }

        if (this.children != null)
        {
            allNodes.addAll(this.children);
        }

        return allNodes;
    }
}
