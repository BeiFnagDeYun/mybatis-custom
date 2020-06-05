package org.demo.acti.service.service.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrativeTest02 implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrativeTest02.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        LOG.info("最终审批");
    }
}
