package com.example.demo;

import io.dapr.spring.messaging.DaprMessagingTemplate;
import io.dapr.workflows.WorkflowActivity;
import io.dapr.workflows.WorkflowActivityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecondActivity implements WorkflowActivity {

//  @Autowired
//  private DaprMessagingTemplate<MyObject> daprMessagingTemplate;

  private final CounterService counterService;

  public SecondActivity(CounterService counterService) {
    this.counterService = counterService;
  }

  @Override
  public Object run(WorkflowActivityContext ctx) {
    System.out.println("Executing the Second activity");
    counterService.increment();
//    daprMessagingTemplate.send("topic", new MyObject("customObjectFromActivity",
//        counterService.getCounter(),
//        new MyNestedObject("value"),
//        MyEnum.VALUE_2));
    return null;
  }
}
