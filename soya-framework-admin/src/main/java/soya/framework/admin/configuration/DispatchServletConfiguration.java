package soya.framework.admin.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.dispatch.servlet.DispatchServlet;

@Configuration
public class DispatchServletConfiguration {
    @Bean
    public ServletRegistrationBean dispatchServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new DispatchServlet(), "/api/*");
        bean.setLoadOnStartup(10);

        return bean;
    }
}
