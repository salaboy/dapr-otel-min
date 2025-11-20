package com.example.demo;


import io.dapr.workflows.Workflow;
import io.dapr.workflows.WorkflowStub;
import org.springframework.stereotype.Component;

@Component
public class SimpleWorkflow implements Workflow {

  @Override
  public WorkflowStub create() {
    return ctx -> {
      String instanceId = ctx.getInstanceId();

      ctx.getLogger().info("> Workflow started {}", instanceId);

      ctx.callActivity(FirstActivity.class.getName()).await();

      ctx.getLogger().info("> Waiting for external event");
      String event = ctx.waitForExternalEvent("ExternalEvent", String.class).await();

      ctx.getLogger().info("> External event received: {}", event);

      if(event.equals("happy")) {
        ctx.callActivity(SecondActivity.class.getName()).await();
      } else{
        ctx.getLogger().info("> No SecondActivity Executed. {}", instanceId);
      }

      ctx.getLogger().info("> Workflow Completed {}", instanceId);
      ctx.complete("Workflow completed");
    };
  }
}

//System.out.println(">>> Workflow NOW -> " + LocalTime.now());