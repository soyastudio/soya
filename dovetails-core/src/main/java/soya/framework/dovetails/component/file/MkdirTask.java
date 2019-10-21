package soya.framework.dovetails.component.file;

import soya.framework.dovetails.Task;
import soya.framework.dovetails.TaskSession;

import java.io.File;

public final class MkdirTask extends Task {
    private static final int MKDIR_RETRY_SLEEP_MILLIS = 10;

    File dir;

    protected MkdirTask(String uri) {
        super(uri);
    }

    @Override
    public void process(TaskSession session) throws Exception {
        if (dir == null) {
            throw new Exception("dir attribute is required");
        }

        if (dir.isFile()) {
            throw new Exception(
                    "Unable to create directory as a file already exists with that name: " +
                            dir.getAbsolutePath());
        }

        if (!dir.exists()) {
            boolean result = mkdirs(dir);
            if (!result) {
                if (dir.exists()) {
                    return;
                }
            }
            //log("Created dir: " + dir.getAbsolutePath());
        } else {
            //log("Skipping " + dir.getAbsolutePath() + " because it already exists.", Project.MSG_VERBOSE);
        }
    }

    private boolean mkdirs(File f) {
        if (!f.mkdirs()) {
            try {
                Thread.sleep(MKDIR_RETRY_SLEEP_MILLIS);
                return f.mkdirs();
            } catch (InterruptedException ex) {
                return f.mkdirs();
            }
        }
        return true;
    }
}
