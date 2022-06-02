package soya.framework.commandline;

import java.io.File;
import java.io.IOException;

public interface TaskResultExporter {
    void export(TaskResult result, File dir) throws IOException;
}
