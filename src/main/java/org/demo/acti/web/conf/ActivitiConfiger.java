package org.demo.acti.web.conf;

import org.activiti.engine.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.concurrent.ExecutorService;

@Configuration
public class ActivitiConfiger {

    @Bean
    public DefaultAsyncJobExecutor defaultAsyncJobExecutor(ExecutorService executorService) {
        DefaultAsyncJobExecutor executor = new DefaultAsyncJobExecutor();
        executor.setExecutorService(executorService);
        return executor;
    }

    @Bean
    public ThreadPoolExecutorFactoryBean threadPoolExecutorFactoryBean() {
        ThreadPoolExecutorFactoryBean factoryBean = new ThreadPoolExecutorFactoryBean();
        factoryBean.setThreadNamePrefix("activiti-job-");
        factoryBean.setCorePoolSize(3);
        factoryBean.setMaxPoolSize(100);
        factoryBean.setQueueCapacity(100);
        return factoryBean;
    }

}
