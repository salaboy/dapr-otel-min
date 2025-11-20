package com.example.demo;


import io.dapr.spring.messaging.DaprMessagingTemplate;
import io.dapr.workflows.client.DaprWorkflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoRestController {

  @Autowired
  private DaprWorkflowClient daprWorklfowClient;

//  @Autowired
//  private DaprMessagingTemplate<MyObject>  daprMessagingTemplate;



  @PostMapping("/start")
  public String start() {
//    daprMessagingTemplate.send("topic", new MyObject("customObjectFromRestController",
//        0,
//        new MyNestedObject("value"),
//        MyEnum.VALUE_1));
    return daprWorklfowClient.scheduleNewWorkflow(SimpleWorkflow.class);

  }

  @PostMapping("/{workflowId}/{event}")
  public void event(@PathVariable String workflowId, @PathVariable String event){
    daprWorklfowClient.raiseEvent(workflowId, "ExternalEvent", event);
  }
}
