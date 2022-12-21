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

package org.finos.legend.engine.plan.execution.stores.document.test.document;

import org.junit.Assert;
import org.junit.Test;

public class TestDocumentMapping extends AbstractTestDocument
{

    private static final String documentMapping = "simple::mapping::document";
    private static final String documentRuntime = "simple::runtime::document";

//    @Test
//    public void testDocumentMapping()
//    {
//        String actualMongoPlan = this.buildExecutionPlanString("mongo::document::test", documentMapping, documentRuntime);
//        String expectedMongoPlan = "";
//
//        Assert.assertEquals(true, true);
//    }

    public String modelResourcePath()
    {
        return "/org/finos/legend/engine/plan/execution/stores/document/test/mongo/simpleMapping.pure";
    }
}