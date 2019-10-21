package soya.framework.dovetails.component.log;

import soya.framework.DataObject;
import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class LogTask extends Task {

    private File logFile;

    protected LogTask(String uri) {
        super(uri);
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public void process(TaskSession session) throws IOException {

        DataObject dataObject = session.getCurrentState();
        if (this.getName() != null && getName().trim().length() > 0) {
            dataObject = (DataObject) session.get(getName());
        }

        if (logFile != null) {
            if (!logFile.exists()) {
                File dir = logFile.getParentFile();
                dir.mkdirs();

                logFile.createNewFile();
            }

            FileWriter writer = new FileWriter(logFile);
            writer.write(dataObject.getAsString());
            writer.flush();
        }
    }
}
