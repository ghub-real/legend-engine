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

public class DocumentQueryExecutionNode extends ExecutionNode
{
    public String mongoQLQuery;
    public String mongoQLQuery2;
    public String onConnectionCloseCommitQuery;
    public String onConnectionCloseRollbackQuery;
    public DatabaseConnection connection;
    public List<DocumentQueryResultField> resultFields;

    public DocumentQueryExecutionNode()
    {
    }

    public DocumentQueryExecutionNode(String mongoQLQuery, String mongoQLQuery2, String onConnectionCloseCommitQuery, String onConnectionCloseRollbackQuery, DatabaseConnection connection, List<DocumentQueryResultField> resultFields)
    {
        this.mongoQLQuery = mongoQLQuery;
        this.mongoQLQuery2 = mongoQLQuery2;
        this.onConnectionCloseCommitQuery = onConnectionCloseCommitQuery;
        this.onConnectionCloseRollbackQuery = onConnectionCloseRollbackQuery;
        this.connection = connection;
        this.resultFields = resultFields;
    }

    @Override
    public <T> T accept(ExecutionNodeVisitor<T> executionNodeVisitor)
    {
        return executionNodeVisitor.visit(this);
    }

    public void setMongoQLQuery(String mongoQLQuery)
    {
        this.mongoQLQuery = mongoQLQuery;
    }

    public void setMongoQLQuery2(String mongoQLQuery2)
    {
        this.mongoQLQuery2 = mongoQLQuery2;
    }

    public void setOnConnectionCloseCommitQuery(String onConnectionCloseCommitQuery)
    {
        this.onConnectionCloseCommitQuery = onConnectionCloseCommitQuery;
    }

    public void setOnConnectionCloseRollbackQuery(String onConnectionCloseRollbackQuery)
    {
        this.onConnectionCloseRollbackQuery = onConnectionCloseRollbackQuery;
    }

    public void setConnection(DatabaseConnection connection)
    {
        this.connection = connection;
    }

    public void setResultFields(List<DocumentQueryResultField> resultFields)
    {
        this.resultFields = resultFields;
    }

    public String getMongoQLQuery()
    {
        return mongoQLQuery;
    }

    public String getMongoQLQuery2()
    {
        return mongoQLQuery2;
    }

    public String getOnConnectionCloseCommitQuery()
    {
        return onConnectionCloseCommitQuery;
    }

    public String getOnConnectionCloseRollbackQuery()
    {
        return onConnectionCloseRollbackQuery;
    }

    public DatabaseConnection getConnection()
    {
        return connection;
    }

    public List<DocumentQueryResultField> getResultFields()
    {
        return resultFields;
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
        return resultFields;
    }
}