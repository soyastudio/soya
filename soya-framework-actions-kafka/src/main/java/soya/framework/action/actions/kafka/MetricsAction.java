package soya.framework.action.actions.kafka;

import org.apache.kafka.common.Metric;
import soya.framework.action.Command;

import java.util.Collection;

@Command(group = "kafka", name = "metrics", httpMethod = Command.HttpMethod.GET)
public class MetricsAction extends KafkaAction<Metric[]> {

    @Override
    protected Metric[] execute() throws Exception {
        Collection<? extends Metric> results = adminClient().metrics().values();
        return results.toArray(new Metric[results.size()]);
    }
}
