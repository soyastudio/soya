package com.albertsons.specright.rest;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration() {
        register(MultiPartFeature.class);
        packages(SpecrightController.class.getPackage().getName());
        swaggerConfig();
    }

    private Swagger swaggerConfig() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig swaggerConfigBean = new BeanConfig();
        swaggerConfigBean.setConfigId("Specright");
        swaggerConfigBean.setTitle("Specright API Scan Service");
        //swaggerConfigBean.setVersion("v1");
        swaggerConfigBean.setSchemes(new String[]{"http", "https"});
        swaggerConfigBean.setBasePath("/api");
        swaggerConfigBean.setResourcePackage(SpecrightController.class.getPackage().getName());
        swaggerConfigBean.setPrettyPrint(true);
        swaggerConfigBean.setScan(true);

        return swaggerConfigBean.getSwagger();
    }

}
