package soya.framework.action;

import java.io.File;
import java.io.IOException;

public interface TaskResultExporter {
    void export(Object result, File dir) throws IOException;
}
