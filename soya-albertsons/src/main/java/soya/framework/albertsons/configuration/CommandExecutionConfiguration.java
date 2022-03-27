package soya.framework.albertsons.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.commons.cli.CommandExecutionContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CommandExecutionConfiguration {

    @Value("${workspace.home}")
    private String workspaceHome;

    @Bean
    ExecutorService commandExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    CommandExecutionContext commandExecutionContext(ExecutorService service) {
        return CommandExecutionContext.builder()
                .setExecutorService(service)
                .setProperty("workspace.home", workspaceHome)
                .create();
    }
}
