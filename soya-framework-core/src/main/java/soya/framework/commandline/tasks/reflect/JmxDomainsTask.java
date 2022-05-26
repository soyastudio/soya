package soya.framework.commandline.tasks.reflect;

import soya.framework.commandline.Command;

import java.lang.management.ManagementFactory;

@Command(group = "reflect", name = "jmx-domains", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class JmxDomainsTask extends ReflectionTask<String[]> {

    @Override
    public String[] execute() throws Exception {
        return ManagementFactory.getPlatformMBeanServer().getDomains();
    }
}
