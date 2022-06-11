package soya.framework.commandline.tasks.ant;

import org.apache.tools.ant.BuildException;
import soya.framework.commandline.TaskResult;
import soya.framework.commandline.TaskResultExporter;

import java.io.File;
import java.io.IOException;

public class Export extends AntTaskExtension {

    private String name;
    private String type;
    private File todir;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public File getTodir() {
        return todir;
    }

    public void setTodir(File todir) {
        this.todir = todir;
    }

    public void execute() throws BuildException {
        if (this.name == null) {
            throw new BuildException("'name' attribute is required", this.getLocation());
        }

        if (this.todir == null) {
            throw new BuildException("'todir' attribute is required", this.getLocation());
        }

        TaskResult taskResult = getProject().getResult(name);
        if (taskResult == null) {
            throw new BuildException("Task result '" + name + "' is null.", this.getLocation());
        }

        prepareDest();

        try {
            getTaskResultExporter().export(taskResult, todir);

        } catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }

    }

    private void prepareDest() throws BuildException {
        if (this.todir == null) {
            throw new BuildException("dir attribute is required", this.getLocation());
        } else if (this.todir.isFile()) {
            throw new BuildException("Unable to create directory as a file already exists with that name: %s", new Object[]{this.todir.getAbsolutePath()});
        } else {
            if (!this.todir.exists()) {
                boolean result = this.mkdirs(this.todir);
                if (!result) {
                    if (this.todir.exists()) {
                        this.log("A different process or task has already created dir " + this.todir.getAbsolutePath(), 3);
                        return;
                    }

                    throw new BuildException("Directory " + this.todir.getAbsolutePath() + " creation was not successful for an unknown reason", this.getLocation());
                }

                this.log("Created dir: " + this.todir.getAbsolutePath());
            } else {
                this.log("Skipping " + this.todir.getAbsolutePath() + " because it already exists.", 3);
            }

        }
    }

    private boolean mkdirs(File f) {
        if (!f.mkdirs()) {
            try {
                Thread.sleep(10L);
                return f.mkdirs();
            } catch (InterruptedException var3) {
                return f.mkdirs();
            }
        } else {
            return true;
        }
    }

    private TaskResultExporter getTaskResultExporter() {
        String exporter = type == null ? "TXT" : type.toUpperCase();
        switch (exporter) {
            case "JAVA":
                return new SimpleJavaExporter();

            default:
                return new SimpleTextExporter();
        }
    }
}
