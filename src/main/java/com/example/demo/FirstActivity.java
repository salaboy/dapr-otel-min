package com.example.demo;

import io.dapr.workflows.WorkflowActivity;
import io.dapr.workflows.WorkflowActivityContext;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FirstActivity implements WorkflowActivity {
  private Logger logger = org.slf4j.LoggerFactory.getLogger(FirstActivity.class);
  private final CounterService counterService;

  public FirstActivity(CounterService counterService) {
    this.counterService = counterService;
  }

  @Override
  public Object run(WorkflowActivityContext ctx) {
    logger.info("Executing the First activity.");
    String traceParentId = ctx.getTraceParentId();

    System.out.println(">>>>> Trace Parent at First Task " + traceParentId);

    //REST Call, Kafka message..
    counterService.increment();

    return null;
  }
}
