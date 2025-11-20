package com.example.demo;

import io.dapr.springboot.DaprAutoConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = { TestDemoApplication.class, DaprTestContainersConfig.class,
    DaprAutoConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DemoApplicationTests {


  @Autowired
  CounterService counterService;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + 8080;
    org.testcontainers.Testcontainers.exposeHostPorts(8080);
    counterService.reset();
  }

	@Test
	void happyPath() {

    String instanceId = given().contentType(ContentType.JSON)
        .when()
        .post("/start")
        .then()
        .statusCode(200).extract().asString();


    // Check that I have an instance id
    assertNotNull(instanceId);

    await().atMost(Duration.ofSeconds(2))
        .pollDelay(100, TimeUnit.MILLISECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .until(() -> {
          return counterService.getCounter() == 1;
        });

    //Test Happy Path
    given().contentType(ContentType.JSON)
        .when()
        .post("/"+instanceId+"/happy")
        .then()
        .statusCode(200);


    await().atMost(Duration.ofSeconds(2))
        .pollDelay(100, TimeUnit.MILLISECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .until(() -> {
          return counterService.getCounter() == 2;
        });

	}


  @Test
  void sadPath() throws InterruptedException {

    String instanceId = given().contentType(ContentType.JSON)
        .when()
        .post("/start")
        .then()
        .statusCode(200).extract().asString();


    // Check that I have an instance id
    assertNotNull(instanceId);

    await().atMost(Duration.ofSeconds(2))
        .pollDelay(100, TimeUnit.MILLISECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .until(() -> {
          return counterService.getCounter() == 1;
        });

    //Test sad Path
    given().contentType(ContentType.JSON)
        .when()
        .post("/"+instanceId+"/sad")
        .then()
        .statusCode(200);

    TimeUnit.SECONDS.sleep(2);

    assertEquals(1, counterService.getCounter());
  }
}
