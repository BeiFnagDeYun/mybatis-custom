package org.demo.acti.service.model.process.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("流程定义实体")
public class ProcessDefEntity {

    @ApiModelProperty("流程定义Key")
    private String processKey;
    @ApiModelProperty("流程定义名称")
    private String processName;


    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
