package org.demo.acti.service.service.process.impl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoProcessEngineLifecycleListener implements ProcessEngineLifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(DemoProcessEngineLifecycleListener.class);

    @Override
    public void onProcessEngineBuilt(ProcessEngine processEngine) {
        LOG.info("{}开始启动", processEngine);
    }

    @Override
    public void onProcessEngineClosed(ProcessEngine processEngine) {
        LOG.info("{}结束", processEngine);
    }
}
