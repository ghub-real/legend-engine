//  Copyright 2022 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.finos.legend.engine.server.test.pureClient.executionPlan;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.finos.legend.engine.server.test.shared.PureTestHelper;
import org.finos.legend.engine.server.test.shared.PureWithEngineHelper;
import org.finos.legend.pure.m3.execution.test.TestCollection;
import org.finos.legend.pure.runtime.java.compiled.execution.CompiledExecutionSupport;

import static org.finos.legend.engine.server.test.shared.PureTestHelper.wrapSuite;

public class Test_ExecutionPlan_JavaPlatform_UsingPureClient extends TestSuite
{
    public static Test suite()
    {
        return wrapSuite(
                true,
                false,
                () -> PureWithEngineHelper.initClientVersionIfNotAlreadySet("vX_X_X"),
                () ->
                {
                    CompiledExecutionSupport executionSupport = PureTestHelper.getClassLoaderExecutionSupport();
                    TestSuite suite = new TestSuite();
                    suite.addTest(PureTestHelper.buildSuite(TestCollection.collectTests("meta::pure::executionPlan::engine::java", executionSupport.getProcessorSupport(), ci -> PureTestHelper.satisfiesConditions(ci, executionSupport.getProcessorSupport())), executionSupport));
                    return suite;
                });
    }
}