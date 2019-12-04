package soya.framework.dovetails.application.service;

import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.Date;

public abstract class DovetailsService implements ApplicationContextAware {
    protected static DovetailsService instance;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Scheduler scheduler;

    protected DovetailsService() {
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

    public static DovetailsService getInstance() {
        return instance;
    }
}
