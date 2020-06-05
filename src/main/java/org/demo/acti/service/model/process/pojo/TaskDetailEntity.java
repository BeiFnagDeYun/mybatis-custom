package org.demo.acti.service.model.process.pojo;

import java.util.Map;

public class TaskDetailEntity extends TaskEntity {

    private Map<String,Object> variables;

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
