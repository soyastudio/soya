package soya.framework.core.tasks.reflect;

import soya.framework.core.Command;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Command(group = "reflect", name = "jmx-mbeans", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class JmxMBeansTask extends ReflectionTask<String[]> {
    @Override
    public String[] execute() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("java.lang:*");
        Set<ObjectInstance> instances = server.queryMBeans(null, null);

        List<String> list = new ArrayList<>();

        instances.forEach(e -> {
            list.add(e.getObjectName().toString());
        });
        Collections.sort(list);

        return list.toArray(new String[list.size()]);
    }
}
