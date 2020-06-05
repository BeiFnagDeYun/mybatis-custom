package org.demo.acti.service.service.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrativeTest03 implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrativeTest03.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        LOG.info("二级自动审批");
    }
}
