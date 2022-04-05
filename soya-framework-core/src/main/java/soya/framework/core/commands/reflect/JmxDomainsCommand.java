package soya.framework.core.commands.reflect;

import soya.framework.core.Command;

import java.lang.management.ManagementFactory;

@Command(group = "reflect", name = "jmx-domains", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class JmxDomainsCommand extends ReflectCommand<String> {

    @Override
    public String call() throws Exception {
        return toJson(ManagementFactory.getPlatformMBeanServer().getDomains());
    }
}
