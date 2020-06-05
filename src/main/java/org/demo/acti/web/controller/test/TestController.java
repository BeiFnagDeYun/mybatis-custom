package org.demo.acti.web.controller.test;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Api(tags = {"测试"})
public class TestController {

    @Autowired
    private RuntimeService runtimeService;

    @GetMapping("/receiveTask/trigger")
    @ApiOperation("接收任务触发")
    public String singl(@RequestParam("processIntanceId") String processIntanceId) {
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processIntanceId).activityId("_3").singleResult();
        runtimeService.trigger(execution.getId());
        return execution.getDescription();
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }
}
