package com.example.demo;

//import io.arconia.dev.services.opentelemetry.OpenTelemetryDevServicesProperties;
//import io.arconia.dev.services.lgtm.LgtmDevServicesProperties;
import io.arconia.dev.services.opentelemetry.OpenTelemetryDevServicesProperties;
import io.dapr.testcontainers.Component;
import io.dapr.testcontainers.Configuration;
import io.dapr.testcontainers.DaprContainer;
import io.dapr.testcontainers.OtelTracingConfigurationSettings;
import io.dapr.testcontainers.TracingConfigurationSettings;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
//import org.testcontainers.grafana.LgtmStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.arconia.dev.services.opentelemetry.OpenTelemetryDevServicesAutoConfiguration.GRPC_PORT;
import static io.arconia.dev.services.opentelemetry.OpenTelemetryDevServicesAutoConfiguration.HTTP_PORT;

@TestConfiguration(proxyBeanMethods = false)
public class DaprTestContainersConfig {

//  @Bean
//  @ServiceConnection
//  LgtmStackContainer lgtmContainer(Network network, LgtmDevServicesProperties properties) {
//    return (LgtmStackContainer)((LgtmStackContainer)((LgtmStackContainer)(new LgtmStackContainer(DockerImageName.parse(properties.getImageName())
//        .asCompatibleSubstituteFor("grafana/otel-lgtm")))
//        .withEnv(properties.getEnvironment()))
//        .withNetwork(network)
//        .withNetworkAliases("lgtm")
//        .withStartupTimeout(properties.getStartupTimeout()))
//        .withReuse(properties.getShared().asBoolean());
//  }


  @Bean
  @ServiceConnection("otel/opentelemetry-collector")
  GenericContainer<?> otelCollectorContainer(OpenTelemetryDevServicesProperties properties, Network network) {

    Map<String, String> env = new HashMap<>(properties.getEnvironment());
    String dash0AuthToken = System.getenv("DASH0_AUTH_TOKEN");
    String dash0EndpointOtlpGrpcHostname = System.getenv("DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME");
    String dash0EndpointOtlpGrpcPort = System.getenv("DASH0_ENDPOINT_OTLP_GRPC_PORT");

    System.out.println("Env Vars for Dash0: " + dash0AuthToken + "\n"
        + dash0EndpointOtlpGrpcHostname + ":" + dash0EndpointOtlpGrpcPort);
    env.put("DASH0_AUTH_TOKEN", dash0AuthToken);


    env.put("DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME", dash0EndpointOtlpGrpcHostname);
    env.put("DASH0_ENDPOINT_OTLP_GRPC_PORT", dash0EndpointOtlpGrpcPort);

    return new GenericContainer<>(DockerImageName.parse(properties.getImageName()))
        .withExposedPorts(GRPC_PORT, HTTP_PORT, 13133)
        .withEnv(env)
        .withCommand("--config=/etc/otel-collector-config.yaml")
        .withCopyFileToContainer(
          MountableFile.forClasspathResource("/otel-config/"),
          "/etc/"
        )
        .withNetwork(network)
        .withNetworkAliases("otel-collector")
        .withStartupTimeout(properties.getStartupTimeout())
        .withReuse(properties.getShared().asBoolean());
  }



  //private DockerImageName daprImage = DockerImageName.parse("docker.io/joshdiagrid/daprd-trace:one").asCompatibleSubstituteFor("daprio/daprd:1.16.0-rc.5");

  //private DockerImageName daprImage = DockerImageName.parse("salaboy/dapr:dev-linux-arm64").asCompatibleSubstituteFor("daprio/daprd:1.16.0-rc.5");
  private DockerImageName daprImage = DockerImageName.parse("kaspernissen/daprd:v2.0-linux-arm64").asCompatibleSubstituteFor("daprio/daprd:1.16.0-rc.5");

  @Bean
  @ServiceConnection
  DaprContainer daprContainer(Network network, GenericContainer<?> otelCollectorContainer) {


    return new DaprContainer(daprImage)
        .withNetwork(network)
        .withAppName("demo-dapr")
        .withAppPort(8080)
        .withComponent(new Component("kvstore", "state.in-memory", "v1",
            Map.of("actorStateStore", "true" )))
        .withAppChannelAddress("host.testcontainers.internal")
        .withConfiguration(
            new Configuration("daprConfig",
                new TracingConfigurationSettings("1",
                    true,
                    new OtelTracingConfigurationSettings(
                        //"lgtm:4317",
                        "otel-collector:4317",
                        false, "grpc"),
                    null), null))
//        .withLogConsumer(outputFrame -> System.out.println(outputFrame.getUtf8String()))
//        .withDaprLogLevel(DaprLogLevel.DEBUG)
        .withAppHealthCheckPath("/actuator/health")
        .dependsOn(otelCollectorContainer);
  }



//  @Bean
//  GenericContainer zipkinContainer(Network network) {
//    GenericContainer zipkinContainer = new GenericContainer(DockerImageName.parse("openzipkin/zipkin:latest"))
//        .withNetwork(network)
//        .withExposedPorts(9411)
//        .withNetworkAliases("zipkin");
//
//    return zipkinContainer;
//  }

  @Bean
  public Network getNetwork(Environment env) {
    boolean reuse = env.getProperty("reuse", Boolean.class, false);
    if (reuse) {
      Network defaultDaprNetwork = new Network() {
        @Override
        public String getId() {
          return "demo-network";
        }

        @Override
        public void close() {

        }

        @Override
        public Statement apply(Statement base, Description description) {
          return null;
        }
      };

      List<com.github.dockerjava.api.model.Network> networks = DockerClientFactory.instance().client().listNetworksCmd()
          .withNameFilter("demo-network").exec();
      if (networks.isEmpty()) {
        Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("dapr-network")).build().getId();
        return defaultDaprNetwork;
      } else {
        return defaultDaprNetwork;
      }
    } else {
      return Network.newNetwork();
    }
  }



}













//  @Bean
//  @ServiceConnection
//  DaprContainer daprContainer(Network network) {
//    return new DaprContainer("daprio/daprd:1.16.0")
//        .withNetwork(network)
//        .withAppName("demo")
//        .withAppPort(8080)
//        .withComponent(new Component("kvstore", "state.in-memory", "v1",
//            Map.of("actorStateStore", "true" )))
//        .withAppChannelAddress("host.testcontainers.internal")
//        .withConfiguration(new Configuration("daprConfig",
//            new TracingConfigurationSettings("1", true, null,
//                new ZipkinTracingConfigurationSettings("http://zipkin:9411/api/v2/spans")), null))
////        .withLogConsumer(outputFrame -> System.out.println(outputFrame.getUtf8String()))
////        .withDaprLogLevel(DaprLogLevel.DEBUG)
//        .withAppHealthCheckPath("/actuator/health");
//  }



//  @Bean
//  GenericContainer zipkinContainer(Network network) {
//    GenericContainer zipkinContainer = new GenericContainer(DockerImageName.parse("openzipkin/zipkin:latest"))
//        .withNetwork(network)
//        .withExposedPorts(9411)
//        .withNetworkAliases("zipkin");
//
//    return zipkinContainer;
//  }
//
//  @Bean
//  public Network getNetwork(Environment env) {
//    boolean reuse = env.getProperty("reuse", Boolean.class, false);
//    if (reuse) {
//      Network defaultDaprNetwork = new Network() {
//        @Override
//        public String getId() {
//          return "demo-network";
//        }
//
//        @Override
//        public void close() {
//
//        }
//
//        @Override
//        public Statement apply(Statement base, Description description) {
//          return null;
//        }
//      };
//
//      List<com.github.dockerjava.api.model.Network> networks = DockerClientFactory.instance().client().listNetworksCmd()
//          .withNameFilter("demo-network").exec();
//      if (networks.isEmpty()) {
//        Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("dapr-network")).build().getId();
//        return defaultDaprNetwork;
//      } else {
//        return defaultDaprNetwork;
//      }
//    } else {
//      return Network.newNetwork();
//    }
//  }


