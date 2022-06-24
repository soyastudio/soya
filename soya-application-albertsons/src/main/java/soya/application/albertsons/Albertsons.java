package soya.application.albertsons;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import soya.framework.commandline.TaskExecutionContext;
import soya.framework.dispatch.servlet.DispatchServlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Albertsons {
    @Value("${workspace.home}")
    private String workspaceHome;

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
    public TaskExecutionContext commandExecutionContext(ExecutorService executorService, ApplicationContext applicationContext) throws BeansException, IOException {

        Properties properties = new Properties();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("kafka-config.properties");
        properties.load(inputStream);


        return TaskExecutionContext.builder()
                .setExecutorService(executorService)
                .serviceLocator(applicationContext)
                .setProperty("workspace.home", workspaceHome)
                .addScanPackages("soya.application.albertsons", "soya.framework.commands.apache.kafka")
                .create();
    }

    public static void main(String[] args) {
        SpringApplication.run(Albertsons.class, args);
    }
}
