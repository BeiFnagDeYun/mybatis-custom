package org.demo.acti.service.model.process.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@ApiModel("开始流程")
public class StartProcessDto {
    @NotBlank
    @ApiModelProperty("流程key")
    private String processKey;
    @NotNull
    @ApiModelProperty("用户ID")
    private Integer userId;
    @ApiModelProperty("标题")
    @NotBlank
    private String title;
    @ApiModelProperty("数据")
    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
