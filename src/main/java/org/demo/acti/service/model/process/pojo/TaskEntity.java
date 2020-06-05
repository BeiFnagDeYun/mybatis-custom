package org.demo.acti.service.model.process.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("任务信息")
public class TaskEntity {

    @ApiModelProperty("任务ID")
    private String taskId;
    @ApiModelProperty("任务名称")
    private String taskName;
    @ApiModelProperty("流程实例ID")
    private String processIntanceId;
    @ApiModelProperty("描述")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProcessIntanceId() {
        return processIntanceId;
    }

    public void setProcessIntanceId(String processIntanceId) {
        this.processIntanceId = processIntanceId;
    }
}
