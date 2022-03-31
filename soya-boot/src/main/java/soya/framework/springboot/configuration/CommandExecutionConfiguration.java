package soya.framework.springboot.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.commons.cli.CommandExecutionContext;

import javax.sql.DataSource;
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

    @Bean
    CommandExecutionContext commandExecutionContext(ExecutorService service) {
        return CommandExecutionContext.builder()
                .setExecutorService(service)
                .setProperty("workspace.home", workspaceHome)
                .setProperty("ant.work.home", antWorkHome)
                .create();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansOfType(DataSource.class);
    }
}
