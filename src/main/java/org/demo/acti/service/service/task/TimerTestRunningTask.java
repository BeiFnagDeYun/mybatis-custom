package org.demo.acti.service.service.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerTestRunningTask implements JavaDelegate {
    Logger LOG = LoggerFactory.getLogger(TimerTestRunningTask.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        LOG.info("TimerTestRunningTask:execute:{}", delegateExecution.getEventName());
    }
}
