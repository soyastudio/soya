package soya.framework.admin.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.core.CommandExecutionContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommandExecutionConfiguration implements ApplicationContextAware {

    @Value("${workspace.home}")
    private String workspaceHome;

    @Value("${ant.work.home}")
    private String antWorkHome;

    @Bean
    ExecutorService commandExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CommandExecutionContext.builder()
                .setExecutorService(applicationContext.getBean(ExecutorService.class))
                .serviceLocator(applicationContext)
                .setProperty("workspace.home", workspaceHome)
                .setProperty("ant.work.home", antWorkHome)
                //.addScanPackages("soya.framework.core.commands")
                .create();
    }

}
