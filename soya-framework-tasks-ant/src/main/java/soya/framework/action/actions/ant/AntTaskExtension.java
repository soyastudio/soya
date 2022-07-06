package soya.framework.action.actions.ant;

import org.apache.tools.ant.Task;

public class AntTaskExtension extends Task {

    @Override
    public ProjectSession getProject() {
        return (ProjectSession) super.getProject();
    }
}
