package soya.application.albertsons;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import soya.framework.core.CommandExecutionContext;
import soya.framework.dispatch.servlet.DispatchServlet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Albertsons {
    @Value("${workspace.home}")
    private String workspaceHome;

    @Value("${ant.work.home}")
    private String antWorkHome;

    @Bean
    ExecutorService commandExecutorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    @DependsOn({"commandExecutionContext"})
    public ServletRegistrationBean dispatchServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new DispatchServlet(), "/api/*");
        bean.setLoadOnStartup(-1);
        return bean;
    }

    @Bean
    public CommandExecutionContext commandExecutionContext(ExecutorService executorService, ApplicationContext applicationContext) throws BeansException {
        return CommandExecutionContext.builder()
                .setExecutorService(executorService)
                .serviceLocator(applicationContext)
                .setProperty("workspace.home", workspaceHome)
                .setProperty("ant.work.home", antWorkHome)
                .addScanPackages("soya.application.albertsons", "soya.framework.commands.apache.kafka")
                .create();
    }

    public static void main(String[] args) {
        SpringApplication.run(Albertsons.class, args);
    }
}
