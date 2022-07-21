package soya.framework.action.actions.reflect;

import com.google.gson.GsonBuilder;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.action.dispatch.ActionProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Command(group = "reflect", name = "dispatcher", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class DispatcherAction extends ReflectionAction<String> {

    @CommandOption(option = "i")
    private String className;

    @Override
    protected String execute() throws Exception {
        Class<?> cls = Class.forName(className);
        ActionProxy actionProxy = cls.getAnnotation(ActionProxy.class);

        DispatcherModel model = new DispatcherModel();
        model.className = className;

        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {


            DispatchMethodModel mm = new DispatchMethodModel();
            mm.methodName = method.getName();
            model.methods.add(mm);
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(model);
    }

    private class DispatcherModel {
        private String className;
        private String group;
        private String path;

        private List<DispatchMethodModel> methods = new ArrayList<>();
    }

    private class DispatchMethodModel {
        private String methodName;

    }
}
