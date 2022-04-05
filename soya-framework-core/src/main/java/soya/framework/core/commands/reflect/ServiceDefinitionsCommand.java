package soya.framework.core.commands.reflect;

import soya.framework.core.Command;
import soya.framework.core.CommandExecutionContext;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

@Command(group = "reflect", name = "serviceDefinitionNames", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class ServiceDefinitionsCommand extends ReflectCommand<String> {

    @Override
    public String call() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        for (String domain : server.getDomains()) {
            System.out.println("======== domain: " + domain);
        }

        ObjectName objectName = new ObjectName("java.lang:*");
        Set<ObjectInstance> instances = server.queryMBeans(objectName, null);
        Iterator<ObjectInstance> iterator = instances.iterator();

        while (iterator.hasNext()) {
            ObjectInstance instance = iterator.next();
            System.out.println("MBean Found:");
            System.out.println("Class Name:\t" + instance.getClassName());
            System.out.println("Object Name:\t" + instance.getObjectName());

            System.out.println("****************************************");
        }


        String[] names = CommandExecutionContext.getInstance().listable().getServiceDefinitionNames();
        return toJson(names);
    }
}
