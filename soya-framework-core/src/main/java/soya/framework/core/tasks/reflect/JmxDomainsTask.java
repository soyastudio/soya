package soya.framework.core.tasks.reflect;

import soya.framework.core.Command;

import java.lang.management.ManagementFactory;

@Command(group = "reflect", name = "jmx-domains", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class JmxDomainsTask extends ReflectionTask<String[]> {

    @Override
    public String[] execute() throws Exception {
        return ManagementFactory.getPlatformMBeanServer().getDomains();
    }
}
