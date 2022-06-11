package soya.framework.commandline.tasks.kafkaya;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.CommandOption;
import soya.framework.commandline.Resource;
import soya.framework.commandline.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@CommandGroup(group = "kafka",
        title = "Kafka Commands",
        description = "Kafka toolkit for executing kafka commands in multiple kafka environments.")
public abstract class KafkaTask<T> extends Task<T> {

    @CommandOption(option = "k", required = true)
    protected String configuration;

    protected AdminClient adminClient() throws IOException {
        return kafkaClient().adminClient();
    }

    protected KafkaProducer producer() throws IOException {
        return kafkaClient().producer();
    }

    protected KafkaConsumer consumer() throws IOException {
        return kafkaClient().consumer();
    }

    protected KafkaClient kafkaClient() throws IOException {
        InputStream inputStream = Resource.create(configuration).getAsInputStream();
        Properties properties = new Properties();
        properties.load(inputStream);

        return KafkaClient.create(properties);
    }

}
