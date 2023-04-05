// Copyright 2023 Goldman Sachs
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
//

package org.finos.legend.engine.plan.execution.stores.mongodb.test.shared;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.finos.legend.pure.generated.Root_meta_pure_functions_io_http_URL;
import org.finos.legend.pure.generated.Root_meta_pure_functions_io_http_URL_Impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Objects;

public class MongoDBCommands
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBCommands.class);
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.1"));

    public static String START_SERVER_FUNCTION = "startMongoDBTestServer_String_1__URL_1_";

    public static Root_meta_pure_functions_io_http_URL startServer(String imageTag)
    {
        System.out.println();
        Root_meta_pure_functions_io_http_URL_Impl url = new Root_meta_pure_functions_io_http_URL_Impl("esUrl");
//        ElasticsearchContainer container = CONTAINERS.computeIfAbsent(imageTag, ElasticsearchCommands::createContainer);
        url._host("localhost");
        url._port(123);
        url._path("/");
        return url;
    }

    public static String STOP_SERVER_FUNCTION = "stopMongoDBTestServer_String_1__Nil_0_";

    public static void stopServer(String imageTag)
    {
        System.out.println("stop");
//        Optional.ofNullable(CONTAINERS.remove(imageTag)).ifPresent(ElasticsearchContainer::stop);
    }

    private static String createContainer(String imageTag)
    {
//        DockerImageName image = DockerImageName.parse(System.getProperty("legend.engine.testcontainer.registry", "docker.elastic.co") + "/elasticsearch/elasticsearch:" + imageTag)
//                .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch:" + imageTag);
//        ElasticsearchContainer container = new ElasticsearchContainer(image);
//
//        long start = System.currentTimeMillis();
//        container.withPassword(getPassword()).start();
//        /*
//        container.followOutput(x ->
//        {
//            try
//            {
//                if (x.getBytes() != null)
//                {
//                    System.err.write(x.getBytes());
//                }
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//        }, OutputFrame.OutputType.STDERR);
//        container.followOutput(x ->
//        {
//            try
//            {
//                if (x.getBytes() != null)
//                {
//                    System.out.write(x.getBytes());
//                }
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//        }, OutputFrame.OutputType.STDOUT);
//        */
        LOGGER.info("ES Test cluster for version {} running on {}.  Took {}ms to start.");
      return "null";
//        return container;
    }

    private static CloseableHttpClient getRestClient()
    {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(30000);

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("mongodb", getPassword()));

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfigBuilder.build())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }

    private static String getPassword()
    {
        return Objects.requireNonNull(System.getProperty("org.finos.legend.engine.plan.execution.stores.mongodb.test.password"), "Missing MongoDB test server password system property");
    }

    private static String execute(HttpUriRequest request)
    {
        try (CloseableHttpClient closeableHttpClient = getRestClient())
        {
            return closeableHttpClient.execute(request, new BasicResponseHandler());
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    public static String REQUEST_SERVER_FUNCTION = "requestMongoDBTestServer_String_1__String_1__String_1_";

    public static String request(String imageTag, String json)
    {
        try
        {
            URI url = URI.create("http://");

            switch (imageTag.charAt(0))
            {
                case '7':
                    return requestV7(url, json);
                default:
                    throw new RuntimeException("Version not supported yet: " + imageTag);
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private static String requestV7(URI url, String json) throws IOException
    {
        return "";
//        RequestBase requestBase = ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports().readValue(json, RequestBase.class);
//        HttpUriRequest httpRequest = requestBase.accept(new ElasticsearchV7RequestToHttpRequestVisitor(url));
//        return execute(httpRequest);
    }
}
