package soya.framework.springboot.starter;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Swagger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import soya.framework.commandline.TaskExecutionContext;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Configuration
@EnableConfigurationProperties(JerseyProperties.class)
@ConditionalOnClass(TaskExecutionContext.class)
@ApplicationPath("/api")
public class JerseyAutoConfiguration extends ResourceConfig {

    @Autowired
    private JerseyProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @PostConstruct
    void init() {
        register(GsonMessageBodyHandler.class);
        register(MultiPartFeature.class);

        packages("soya.application.openbank.api");

        swagger();

    }


    Swagger swagger() {
        BeanConfig swaggerConfigBean = new BeanConfig();
        swaggerConfigBean.setConfigId("Workshop");
        swaggerConfigBean.setTitle("Workshop Server");
        //swaggerConfigBean.setVersion("v1");
        swaggerConfigBean.setContact("wenqun.soya@gmail.com");
        swaggerConfigBean.setSchemes(new String[]{"http"});
        swaggerConfigBean.setBasePath("/api");
        swaggerConfigBean.setResourcePackage("soya.application.openbank.api");
        swaggerConfigBean.setPrettyPrint(true);
        swaggerConfigBean.setScan(true);

        return swaggerConfigBean.getSwagger();
    }
}
