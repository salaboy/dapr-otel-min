# dapr-otel-min


```
export DASH0_AUTH_TOKEN=<YOUR TOKEN>
export DASH0_ENDPOINT_OTLP_GRPC_HOSTNAME=ingress.eu-west-1.aws.dash0.com
export DASH0_ENDPOINT_OTLP_GRPC_PORT=4317
```

To start the app: 
```
./mvnw clean spring-boot:test-run
```

Start Workflow: 

```sh
http POST :8080/start
```
This returns the workflow instance ID that can be used to send an event

With Event Payload `happy` the workflow will execute a second activity:
```sh
http POST :8080/<INSTANCE-ID>/happy
```


With Event Payload `sad` the workflow will complete:
```sh
http POST :8080/<INSTANCE-ID>/sad
```


## Bulding dependencies until all this work is upstream

```java
git clone https://github.com/salaboy/durabletask-java
```

Switch to branch: `trace-context`

Build and publish to local maven repo: 

```
cd client/
../gradlew build
../gradlew publishToMavenLocal
```