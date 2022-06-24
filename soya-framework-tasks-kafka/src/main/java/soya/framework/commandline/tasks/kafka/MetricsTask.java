package soya.framework.commandline.tasks.kafka;

import org.apache.kafka.common.Metric;
import soya.framework.commandline.Command;

import java.util.Collection;

@Command(group = "kafka", name = "metrics", httpMethod = Command.HttpMethod.GET)
public class MetricsTask extends KafkaTask<Metric[]> {

    @Override
    protected Metric[] execute() throws Exception {
        Collection<? extends Metric> results = adminClient().metrics().values();
        return results.toArray(new Metric[results.size()]);
    }
}
