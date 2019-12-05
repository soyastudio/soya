package soya.framework.dovetails.application.service;

import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

public abstract class PipelineService implements ApplicationContextAware {
    protected static PipelineService instance;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Scheduler scheduler;

    protected PipelineService() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    public void execute() {
        System.out.println("--------------------- " + scheduler);
    }

    public static PipelineService getInstance() {
        return instance;
    }
}
