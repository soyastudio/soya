package soya.framework.action.actions.reflect;

import soya.framework.action.Command;
import soya.framework.action.dispatch.ActionDispatchController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "reflect", name = "dispatchers", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class DispatchersAction extends ReflectionAction<String[]> {

    @Override
    protected String[] execute() throws Exception {
        Class<?>[] classes = ActionDispatchController.getInstance().dispatcherInterfaces();
        List<String> list = new ArrayList<>();
        for(Class<?> c : classes) {
            list.add(c.getName());
        }

        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
}
