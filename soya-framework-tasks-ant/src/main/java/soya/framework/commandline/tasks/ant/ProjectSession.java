package soya.framework.commandline.tasks.ant;

import org.apache.tools.ant.Project;
import soya.framework.commandline.TaskResult;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectSession extends Project {

    private Map<String, TaskResult> results = new LinkedHashMap<>();

    public void setResult(String name, TaskResult result) {
        results.put(name, result);
    }

    public TaskResult getResult(String name) {
        return results.get(name);
    }

}
