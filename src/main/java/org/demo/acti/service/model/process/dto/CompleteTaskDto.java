package org.demo.acti.service.model.process.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel("完成任务参数")
public class CompleteTaskDto {

    @ApiModelProperty("流程实例ID")
    private String processIntanceId;
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("审批备注")
    private String comment;
    @ApiModelProperty("流程变量")
    private Map<String, Object> data;

    public CompleteTaskDto() {
    }

    public CompleteTaskDto(String processIntanceId, Integer userId, String comment, Map<String, Object> data) {
        this.processIntanceId = processIntanceId;
        this.userId = userId;
        this.comment = comment;
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getProcessIntanceId() {
        return processIntanceId;
    }

    public void setProcessIntanceId(String processIntanceId) {
        this.processIntanceId = processIntanceId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
