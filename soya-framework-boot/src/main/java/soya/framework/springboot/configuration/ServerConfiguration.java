package soya.framework.springboot.configuration;

import soya.framework.util.FileSystemMonitor;
import soya.framework.util.Heartbeat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfiguration {

    @Bean
    Heartbeat heartbeat() {
        return Heartbeat.builder()
                .name("server-heartbeat")
                .delay(5000l)
                .period(30000l)
                .addListener(new FileSystemMonitor())
                .create();
    }


}
