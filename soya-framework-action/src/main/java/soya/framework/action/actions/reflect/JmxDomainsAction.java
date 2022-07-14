package soya.framework.action.actions.reflect;

import soya.framework.action.Command;

import java.lang.management.ManagementFactory;

@Command(group = "reflect", name = "jmx-domains", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class JmxDomainsAction extends ReflectionAction<String[]> {

    @Override
    public String[] execute() throws Exception {
        return ManagementFactory.getPlatformMBeanServer().getDomains();
    }
}
