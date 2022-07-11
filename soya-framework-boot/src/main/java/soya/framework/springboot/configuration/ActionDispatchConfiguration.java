package soya.framework.springboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import soya.framework.action.dispatch.ActionDispatchController;

@Configuration
public class ActionDispatchConfiguration {

    @Bean
    ActionDispatchController actionDispatchController() {
        return ActionDispatchController.getInstance();
    }

}
