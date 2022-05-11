package soya.framework.springboot.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import soya.framework.core.TaskExecutionContext;
import soya.framework.core.tasks.reflect.ReflectionTask;
import soya.framework.dispatch.servlet.DispatchServlet;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(SoyaProperties.class)
@ConditionalOnClass(TaskExecutionContext.class)
public class SoyaAutoConfiguration {

    @Autowired
    private SoyaProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Bean
    ServletRegistrationBean dispatchServletBean(ApplicationContext applicationContext) throws BeansException {

        ExecutorService executorService = Executors.newFixedThreadPool(properties.getExecutorThreadPoolSize());

        String[] scanPackages = new String[]{ReflectionTask.class.getPackage().getName()};
        if (properties.getScanPackages() != null) {
            scanPackages = properties.getScanPackages().split(",");
            for (int i = 0; i < scanPackages.length; i++) {
                scanPackages[i] = scanPackages[i].trim();
            }
        }

        TaskExecutionContext context = TaskExecutionContext.builder()
                .setExecutorService(executorService)
                .serviceLocator(applicationContext)
                .addScanPackages(scanPackages)
                .create();

        TaskExecutionContext.ExecutionContextHandler handler = (TaskExecutionContext.ExecutionContextHandler) context;
        handler.setRequiredProperty("ant.framework.home", properties.getHome());

        for (String propName : handler.getRequiredProperties()) {
            if (context.getProperty(propName) == null) {
                String propValue = environment.getProperty(propName);
                handler.setRequiredProperty(propName, propValue);

            }
        }

        DispatchServlet servlet = new DispatchServlet();
        servlet.setDebug(properties.isDebug());

        ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/api/*");
        bean.setLoadOnStartup(-1);
        return bean;
    }
}
