package soya.framework.dovetails.component.ant.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import soya.framework.dovetails.ProcessContext;
import soya.framework.dovetails.TaskSession;
import soya.framework.dovetails.component.ant.AdapterException;
import soya.framework.dovetails.component.ant.AntTaskAdapter;
import soya.framework.dovetails.component.ant.AntTaskDef;
import soya.framework.util.ParameterizedText;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public abstract class AntTaskAdapterSupport<T extends Task> implements AntTaskAdapter<T> {
    private static final String ANT_PROJECT_ATTR = "ANT_PROJECT_ATTR";
    private transient File _baseDir;

    private final String name;
    private final ProcessContext context;
    private final Class<T> taskType;

    public AntTaskAdapterSupport(JsonElement attributes, ProcessContext context) {
        this.context = context;
        this.taskType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        AntTaskDef def = getClass().getAnnotation(AntTaskDef.class);
        this.name = def.name();

        if (attributes != null && attributes != null) {
            JsonObject json = attributes.getAsJsonObject();

            for (String attr : def.attributes()) {
                if (json.get(attr) != null) {
                    try {
                        Field field = getField(attr);
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        field.set(this, evaluate(json.get(attr), fieldType, context));

                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        this._baseDir = context.getExternalContext().getBaseDir();
    }

    private Field getField(String name) throws NoSuchFieldException {
        Field field = null;
        Class clazz = getClass();
        try {
            field = clazz.getDeclaredField(name);

        } catch (NoSuchFieldException e) {
            // do nothing
        }

        while (field == null) {
            if(Object.class.equals(clazz)) {
                throw new NoSuchFieldException("No such field: " + name);
            }
            clazz = clazz.getSuperclass();
            try {
                field = clazz.getDeclaredField(name);

            } catch (NoSuchFieldException e) {
                // do nothing
            }
        }

        return field;
    }

    public void execute(TaskSession session) {
        createAntTask(session).execute();
        postExecute(session);
    }

    protected void postExecute(TaskSession session) {

    }

    protected FileSet getFileSet(FileSetModel model) {
        FileSet fileSet = new FileSet();

        fileSet.setProject(new Project());

        fileSet.setDir(getDir(model.getDir()));
        fileSet.setIncludes(model.getIncludes());
        fileSet.setExcludes(model.getExcludes());
        return fileSet;
    }

    protected File getDir(String dir) {
        if (dir == null || dir.trim().length() == 0) {
            return getBaseDir();

        } else if (FileUtils.isAbsolutePath(dir)) {
            return new File(dir);

        } else {

            return new File(getBaseDir(), dir);
        }
    }

    protected File getFile(String file) {
        if (file == null || file.trim().length() == 0) {
            return null;

        } else if (FileUtils.isAbsolutePath(file)) {
            return new File(file);

        } else {

            return new File(getBaseDir(), file);
        }
    }

    protected File getBaseDir() {
        return _baseDir;
    }

    protected T createAntTask(TaskSession session) {
        try {
            T task = taskType.newInstance();
            task.setProject(getProject(session));
            if(task instanceof SessionAwareAntTask) {
                ((SessionAwareAntTask)task).setSession(session);
            }

            init(task, session);
            return task;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AdapterException(e);
        }
    }

    protected abstract void init(T task, TaskSession session);

    private Object evaluate(JsonElement exp, Class<?> type, ProcessContext context) {
        Object result = null;
        if (exp.isJsonPrimitive() && exp.getAsString().contains("${")) {
            String value = exp.getAsString();
            ParameterizedText pt = ParameterizedText.create(value);
            for (String param : pt.getParameters()) {
                pt = pt.evaluate(param, context.getProperty(param));
            }
            result = pt.toString();

        } else {
            result = new Gson().fromJson(exp, type);
        }

        return result;
    }

    private Project getProject(TaskSession session) {
        Project project = (Project) session.get(ANT_PROJECT_ATTR);
        if (project == null) {
            project = new Project();
            session.set(ANT_PROJECT_ATTR, project);
        }
        project.setBaseDir(session.getContext().getExternalContext().getBaseDir());

        return project;
    }
}
