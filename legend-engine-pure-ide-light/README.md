#Pure IDE Light

Pure IDE Light is a development environment for Pure, the language underlying the Legend platform.


##Running Pure IDE Light

From the root of legend-engine, run the following to launch the Pure IDE Light server.

```
mvn -pl legend-engine-pure-ide-light exec:java -Dexec.mainClass="org.finos.legend.engine.ide.PureIDELight" -Dexec.args="server ./legend-engine-pure-ide-light/src/main/resources/ideLightConfig.json"
```

Then navigate to http://127.0.0.1/ide



To Run pure IDE executions and debug in legend-engine java service follow below instructions:


Steps on executing Pure relational/sql projections, hitting local H2 and debugging in Java legend-engine



1. PureIDELight.java

add the following method:

    public static void enableEngineIntegration() {

            System.setProperty("alloy.test.server.host", "127.0.0.1");
            System.setProperty("alloy.test.server.port", "6060");
            System.setProperty("alloy.test.h2.port", "9092");
            System.setProperty("alloy.test.clientVersion", "vX_X_X");
            System.setProperty("alloy.test.serverVersion", "v1");
            System.setProperty("alloy.test.serializationKind", "json");

            System.setProperty("legend.test.server.host", "127.0.0.1");
            System.setProperty("legend.test.server.port", "6060");
            System.setProperty("legend.test.h2.port", "9092");
            System.setProperty("legend.test.clientVersion", "vX_X_X");
            System.setProperty("legend.test.serverVersion", "v1");
            System.setProperty("legend.test.serializationKind", "json");
    }

and make sure you invoke it before the server runs.

    public static void main(String[] args) throws Exception
    {
    enableEngineIntegration();
    new PureIDELight().run(args.length == 0 ? new String[] {"server", "legend-engine-pure-ide-light/src/main/resources/ideLightConfig.json"} : args);
    }


2. Server.java

Instantiate an localH2Server on port 9092:

a. Add h2Server to your class properties:

    private Environment environment;
    private org.h2.tools.Server h2Server;


b. add the following method:

    private void setupLocalH2Db()
    {
    long start = System.currentTimeMillis();
    System.out.println("Starting setup of dynamic connection for database: H2 ");

        int relationalDBPort = 9092;
        try
        {
            h2Server =  AlloyH2Server.startServer(relationalDBPort);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        long end = System.currentTimeMillis();

        System.out.println("Completed setup of dynamic connection for database: H2 on port:" + relationalDBPort + " , time taken(ms):" + (end - start));
    }


c. Invoke it as part of your start up


    // localh2server on 9092
    setupLocalH2Db();

    // GraphQL
    environment.jersey().register(new GraphQLGrammar());




3. userTestConfig.json

a. change metadataserver port from 8080 to 9010

    "metadataserver": {
    "pure": {
      "host": "127.0.0.1",
      "port": 9010
    },



4. Start your legend-engine server  in debug ( Server.java)

5. Start your legend-engine PureIDeLight server and open your IDE via http://localhost:9010/ide/


6. in your welcome func add a sample test execution against in-memory H2 (now hitting your legend-engine server with debug)


    function go():Any[*]
    {
	    meta::relational::tests::tds::setUp();
	    meta::relational::tests::tds::groupBy::simpleGroupByMax();
    }


7. Add a breakpoint to Execute.java lines around:


    public Response exec(Function<PureModel, LambdaFunction<?>> functionFunc, Function0<PureModel> pureModelFunc, PlanExecutor planExecutor, String mapping, Runtime runtime, ExecutionContext context, String clientVersion, MutableList<CommonProfile> pm, String user, SerializationFormat format)
    {
    try
    ...

this should now pause when you execute your go function from your Pure IDE.




