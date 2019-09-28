package soya.framework.dovetails.component.bean.processors;

import soya.framework.dovetails.Predefined;
import soya.framework.dovetails.TaskProcessor;
import soya.framework.dovetails.TaskSession;
import soya.framework.support.JsonData;
import soya.framework.util.IOUtils;

import java.io.InputStream;

@Predefined("CLASSPATH_RESOURCE_READER")
public class ClasspathResourceReader implements TaskProcessor {

    private String path;

    @Override
    public void process(TaskSession session) throws Exception {
        if (path == null) {
            throw new IllegalArgumentException("'path' is not set.");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }

        InputStream inStream = classLoader.getResourceAsStream(path);
        String json = IOUtils.toString(inStream);
        session.updateState(JsonData.fromJson(json));
    }
}
