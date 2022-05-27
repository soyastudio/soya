package soya.framework.albertsons.configuration;

import org.springframework.context.annotation.Configuration;
import soya.framework.commandline.tasks.kafka.KafkaClientFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class KafkaConfiguration {
    public KafkaConfiguration() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("kafka-config.properties");
        properties.load(inputStream);
        KafkaClientFactory.configure(properties);
    }
}
