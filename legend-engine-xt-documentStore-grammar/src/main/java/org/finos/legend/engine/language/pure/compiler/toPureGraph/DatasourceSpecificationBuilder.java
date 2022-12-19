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

package org.finos.legend.engine.language.pure.compiler.toPureGraph;

import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.DatasourceSpecificationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.MongoDBDatasourceSpecification;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.nonrelational.connection.specification.StaticDatasourceSpecification;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_MongoDBDatasourceSpecification;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_MongoDBDatasourceSpecification_Impl;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_StaticDatasourceSpecification;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_StaticDatasourceSpecification_Impl;
import org.finos.legend.pure.generated.Root_meta_external_store_document_runtime_connections_specification_DatasourceSpecification;

public class DatasourceSpecificationBuilder implements DatasourceSpecificationVisitor<Root_meta_external_store_document_runtime_connections_specification_DatasourceSpecification>
{
    private final CompileContext context;

    public DatasourceSpecificationBuilder(CompileContext context)
    {
        this.context = context;
    }

    @Override
    public Root_meta_external_store_document_runtime_connections_specification_DatasourceSpecification visit(DatasourceSpecification datasourceSpecification)
    {
        if (datasourceSpecification instanceof MongoDBDatasourceSpecification)
        {
            MongoDBDatasourceSpecification localH2DatasourceSpecification = (MongoDBDatasourceSpecification) datasourceSpecification;
            Root_meta_external_store_document_runtime_connections_MongoDBDatasourceSpecification local = new Root_meta_external_store_document_runtime_connections_MongoDBDatasourceSpecification_Impl("", null, context.pureModel.getClass("meta::external::store::document::runtime::connections::MongoDBDatasourceSpecification"));
//            local._testDataSetupCsv(localH2DatasourceSpecification.testDataSetupCsv);
//            local._testDataSetupSqls(localH2DatasourceSpecification.testDataSetupSqls == null ? FastList.newList() : FastList.newList(localH2DatasourceSpecification.testDataSetupSqls));
            return local;
        }
        else if (datasourceSpecification instanceof StaticDatasourceSpecification)
        {
            StaticDatasourceSpecification staticDatasourceSpecification = (StaticDatasourceSpecification) datasourceSpecification;
            Root_meta_external_store_document_runtime_connections_StaticDatasourceSpecification _static = new Root_meta_external_store_document_runtime_connections_StaticDatasourceSpecification_Impl("", null, context.pureModel.getClass("meta::pure::alloy::connections::alloy::specification::StaticDatasourceSpecification"));
            _static._host(staticDatasourceSpecification.host);
            _static._port(staticDatasourceSpecification.port);
            _static._databaseName(staticDatasourceSpecification.databaseName);
            return _static;
        }
        return null;
    }
}