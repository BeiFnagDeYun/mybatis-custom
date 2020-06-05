package org.demo.acti.service.service.task;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.demo.acti.service.service.process.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class TaskStartListerner implements TaskListener {
    private static final Logger LOG = LoggerFactory.getLogger(TaskStartListerner.class);

    @Autowired
    private IProcessService processService;

    @Override
    public void notify(DelegateTask delegateTask) {
//        设置单人
//        delegateTask.setAssignee("10");
        // 会签 单一通过则全部通过
        delegateTask.addCandidateUser("10");
        delegateTask.addCandidateUser("11");
        delegateTask.addCandidateUser("12");
        LOG.info("{}事件触发", delegateTask.getEventName());
    }
}
