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

package org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.DatabaseConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.model.result.DocumentQueryResultField;

import java.util.Collections;
import java.util.List;

public class MongoQLExecutionNode extends ExecutionNode
{
    public String mongoQLQuery;
    public String onConnectionCloseCommitQuery;
    public String onConnectionCloseRollbackQuery;
    public DatabaseConnection connection;
    public List<DocumentQueryResultField> resultColumns = Collections.emptyList();

    public DatabaseConnection getConnection()
    {
        return connection;
    }

    @Override
    public <T> T accept(ExecutionNodeVisitor<T> executionNodeVisitor)
    {
        return executionNodeVisitor.visit(this);
    }

    /* Database APIs */
    public String getQuery()
    {
        return this.mongoQLQuery;
    }

    @JsonIgnore
    public String getDatabaseTypeName()
    {
        return this.connection.type.name();
    }

    @JsonIgnore
    public String getDatabaseTimeZone()
    {
        return this.connection.timeZone;
    }

    @JsonIgnore
    public List<DocumentQueryResultField> getSQLResultColumns()
    {
        return ListIterate.collect(this.resultColumns, DocumentQueryResultField::new);
    }
}