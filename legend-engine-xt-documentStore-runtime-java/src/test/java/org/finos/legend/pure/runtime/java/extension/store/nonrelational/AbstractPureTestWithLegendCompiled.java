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

package org.finos.legend.pure.runtime.java.extension.store.nonrelational;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.pure.configuration.PureRepositoriesExternal;
import org.finos.legend.pure.m3.SourceMutation;
import org.finos.legend.pure.m3.compiler.Context;
import org.finos.legend.pure.m3.exception.PureExecutionException;
import org.finos.legend.pure.m3.execution.FunctionExecution;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m3.serialization.filesystem.PureCodeStorage;
import org.finos.legend.pure.m3.serialization.filesystem.repository.CodeRepository;
import org.finos.legend.pure.m3.serialization.filesystem.usercodestorage.CodeStorage;
import org.finos.legend.pure.m3.serialization.filesystem.usercodestorage.classpath.ClassLoaderCodeStorage;
import org.finos.legend.pure.m3.serialization.runtime.Message;
import org.finos.legend.pure.m3.serialization.runtime.PureRuntime;
import org.finos.legend.pure.m3.serialization.runtime.PureRuntimeBuilder;
import org.finos.legend.pure.m3.serialization.runtime.VoidPureRuntimeStatus;
import org.finos.legend.pure.m4.ModelRepository;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;
import org.finos.legend.pure.m4.coreinstance.SourceInformation;
import org.finos.legend.pure.m4.exception.PureException;
import org.junit.Assert;
import org.junit.Ignore;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AbstractPureTestWithLegendCompiled // extends AbstractPureTestWithCoreCompiled
{

    protected static PureRuntime runtime;
    protected static ModelRepository repository;
    protected static Context context;
    protected static ProcessorSupport processorSupport;
    protected static FunctionExecution functionExecution;

    //@BeforeClass
    public static void setUpRuntime(FunctionExecution fe)
    {
        RichIterable<CodeRepository> repositories = PureRepositoriesExternal.repositories().select(p -> !p.getName().startsWith("other_") && !p.getName().startsWith("test_"));
        System.out.println(repositories.collect(CodeRepository::getName).makeString(", "));
        PureCodeStorage codeStorage = new PureCodeStorage(null, new ClassLoaderCodeStorage(repositories));
        functionExecution = fe;
        functionExecution.getConsole().disable();
        runtime = new PureRuntimeBuilder(codeStorage)
                .withRuntimeStatus(VoidPureRuntimeStatus.VOID_PURE_RUNTIME_STATUS)
                .setTransactionalByDefault(true)
                .build();
        functionExecution.init(runtime, new Message(""));
        runtime.loadAndCompileCore();
        runtime.loadAndCompileSystem();
        repository = runtime.getModelRepository();
        context = runtime.getContext();
        processorSupport = functionExecution.getProcessorSupport() == null ? runtime.getProcessorSupport() : functionExecution.getProcessorSupport();
        if (functionExecution.getConsole() != null)
        {
            functionExecution.getConsole().enableBufferLines();
        }
    }

    protected static void compileTestSource(String source)
    {
        compileTestSource("testSource_" + UUID.randomUUID().toString().replace('-', '_') + CodeStorage.PURE_FILE_EXTENSION, source);
    }

    protected static SourceMutation compileTestSource(String sourceId, String source)
    {
        return runtime.createInMemoryAndCompile(Tuples.pair(sourceId, source));
    }

    protected static String readTextResource(String resourceName)
    {
        return readTextResource(resourceName, Thread.currentThread().getContextClassLoader());
    }

    protected static String readTextResource(String resourceName, ClassLoader classLoader)
    {
        URL url = classLoader.getResource(resourceName);
        if (url == null)
        {
            throw new RuntimeException("Could not find resource: " + resourceName);
        }
        try (Reader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))
        {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer)) != -1)
            {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Error reading resource from " + url, e);
        }
    }

    protected static void assertPureException(Class<? extends PureException> expectedClass, String expectedInfo, String expectedSource, Integer expectedLine, Integer expectedColumn, Integer expectedEndLine, Integer expectedEndColumn, Exception exception)
    {
        PureException pe = PureException.findPureException(exception);
        Assert.assertNotNull("No Pure exception", pe);
        assertPureException(expectedClass, expectedInfo, expectedSource, expectedLine, expectedColumn, expectedEndLine, expectedEndColumn, pe);
    }

    protected static void assertPureException(Class<? extends PureException> expectedClass, String expectedInfo, String expectedSource, Integer expectedLine, Integer expectedColumn, Integer expectedEndLine, Integer expectedEndColumn, PureException exception)
    {
        assertPureException(expectedClass, expectedInfo, expectedSource, null, null, expectedLine, expectedColumn, expectedEndLine, expectedEndColumn, exception);
    }

    protected static void assertPureException(Class<? extends PureException> expectedClass, String expectedInfo, String expectedSource, Integer expectedStartLine, Integer expectedStartColumn, Integer expectedLine, Integer expectedColumn, Integer expectedEndLine, Integer expectedEndColumn, PureException exception)
    {
        // Check class
        if (expectedClass != null)
        {
            Assert.assertTrue("Expected an exception of type " + expectedClass.getCanonicalName() + ", got: " + exception.getClass().getCanonicalName() + " message:" + exception.getMessage(),
                    expectedClass.isInstance(exception));
        }

        // Check info
        if (expectedInfo != null)
        {
            Assert.assertEquals(expectedInfo, exception.getInfo());
        }

        // Check source information
        assertSourceInformation(expectedSource, expectedStartLine, expectedStartColumn, expectedLine, expectedColumn, expectedEndLine, expectedEndColumn, exception.getSourceInformation());
    }

    protected static void assertSourceInformation(String expectedSource, Integer expectedStartLine, Integer expectedStartColumn, Integer expectedLine, Integer expectedColumn, Integer expectedEndLine, Integer expectedEndColumn, SourceInformation sourceInfo)
    {
        if (expectedSource != null)
        {
            Assert.assertEquals("Wrong source", expectedSource, sourceInfo.getSourceId());
        }
        if (expectedStartLine != null)
        {
            Assert.assertEquals("Wrong start line", expectedStartLine.intValue(), sourceInfo.getStartLine());
        }
        if (expectedStartColumn != null)
        {
            Assert.assertEquals("Wrong start column", expectedStartColumn.intValue(), sourceInfo.getStartColumn());
        }
        if (expectedLine != null)
        {
            Assert.assertEquals("Wrong line", expectedLine.intValue(), sourceInfo.getLine());
        }
        if (expectedColumn != null)
        {
            Assert.assertEquals("Wrong column", expectedColumn.intValue(), sourceInfo.getColumn());
        }
        if (expectedEndLine != null)
        {
            Assert.assertEquals("Wrong end line", expectedEndLine.intValue(), sourceInfo.getEndLine());
        }
        if (expectedEndColumn != null)
        {
            Assert.assertEquals("Wrong end column", expectedEndColumn.intValue(), sourceInfo.getEndColumn());
        }
    }

    @Ignore
    public void testExecuteInDbError() throws Exception
    {
        this.compileTestSource(
                "import meta::external::store::document::runtime::connections::*;\n" +
                        "function test():Any[0..1]\n" +
                        "{\n" +
                        "let dataStore = meta::external::store::document::tests::object::TestMongoStore();" +
                        "let documentStoreConnection = ^meta::external::store::document::runtime::connections::DocumentStoreConnection(\n" +
                        "        element = $dataStore,\n" +
                        "        type = meta::external::store::document::metamodel::runtime::DatabaseType.Mongo,\n" +
                        "        datasourceSpecification = ^meta::external::store::document::runtime::connections::MongoDBDatasourceSpecification(host='local',port=27017,databaseName='myCollection'),\n" +
                        "        authenticationStrategy = ^meta::external::store::document::runtime::authentication::TestDatabaseAuthenticationStrategy(),\n" +
                        "        timeZone = 'GMT'\n" +
                        "    );" +
                        "meta::external::store::document::metamodel::execute::executeInDb('select * from tt', $documentStoreConnection, 0, 1000);\n" +
                        "}\n" +
                        "###Relational\n" +
                        "Database mydb()\n"
        );
        try
        {
            this.compileAndExecute("test():Any[0..1]");
        }
        catch (PureExecutionException ex)
        {
            this.assertPureException(PureExecutionException.class, "Error executing sql query; SQL reason: Table \"TT\" not found; SQL statement:\n" +
                    "select * from tt [42102-197]; SQL error code: 42102; SQL state: 42S02", 8, 4, ex);
        }
    }

    protected CoreInstance compileAndExecute(String functionIdOrDescriptor, CoreInstance... parameters)
    {
        runtime.compile();
        return this.execute(functionIdOrDescriptor, parameters);
    }

    protected CoreInstance execute(String functionIdOrDescriptor, CoreInstance... parameters)
    {
        CoreInstance function = runtime.getFunction(functionIdOrDescriptor);
        if (function == null)
        {
            throw new RuntimeException("The function '" + functionIdOrDescriptor + "' can't be found");
        }
        functionExecution.getConsole().clear();
        return functionExecution.start(function, ArrayAdapter.adapt(parameters));
    }

    protected void assertPureException(Class<? extends PureException> expectedClass, String expectedInfo, Integer expectedLine, Integer expectedColumn, Exception exception)
    {
        assertPureException(expectedClass, expectedInfo, null, expectedLine, expectedColumn, null, null, exception);
    }

    protected void assertPureException(String expectedInfo, Exception exception)
    {
        assertPureException(null, expectedInfo, null, null, null, null, null, exception);
    }

    protected void compileTestSourceFromResource(String resourceName, String pureSourceName)
    {
        String code = readTextResource(resourceName);
        compileTestSource(pureSourceName == null ? resourceName : pureSourceName, code);
    }

    protected void compileTestSourceFromResource(String resourceName)
    {
        this.compileTestSourceFromResource(resourceName, (String) null);
    }

}
