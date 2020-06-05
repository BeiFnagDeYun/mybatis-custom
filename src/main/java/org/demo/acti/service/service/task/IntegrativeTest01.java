package org.demo.acti.service.service.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrativeTest01 implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrativeTest01.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        LOG.info("并行网关2");
    }
}
