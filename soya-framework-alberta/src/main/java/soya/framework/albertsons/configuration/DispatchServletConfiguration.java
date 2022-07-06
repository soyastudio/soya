package soya.framework.albertsons.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import soya.framework.action.servlet.ActionServlet;

//@Configuration
public class DispatchServletConfiguration {
    @Bean
    public ServletRegistrationBean dispatchServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new ActionServlet(), "/dispatch/*");
        bean.setLoadOnStartup(10);

        return bean;
    }
}
