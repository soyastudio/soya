package soya.framework.dovetails.support;

import soya.framework.dovetails.TaskSession;

import java.io.File;

public class TaskSessionUtils {

    private static final boolean ON_NETWARE = Os.isFamily("netware");
    private static final boolean ON_DOS = Os.isFamily("dos");
    private static final boolean ON_WIN9X = Os.isFamily("win9x");
    private static final boolean ON_WINDOWS = Os.isFamily("windows");


    public static File getDirectory(String dir, TaskSession session) {
        if (dir == null || dir.trim().length() == 0) {
            return getBaseDir(session);

        } else if (isAbsolutePath(dir)) {
            return new File(dir);

        } else {
            return new File(getBaseDir(session), dir);

        }
    }

    protected File getFile(String file, TaskSession session) {
        if (file == null || file.trim().length() == 0) {
            return null;

        } else if (isAbsolutePath(file)) {
            return new File(file);

        } else {

            return new File(getBaseDir(session), file);
        }
    }

    public static File getBaseDir(TaskSession session) {
        File baseDir = session.getContext().getExternalContext().getBaseDir();

        return baseDir;
    }

    public static boolean isAbsolutePath(String filename) {
        if (filename.isEmpty()) {
            return false;
        } else {
            int len = filename.length();
            char sep = File.separatorChar;
            filename = filename.replace('/', sep).replace('\\', sep);
            char c = filename.charAt(0);
            if (!ON_DOS && !ON_NETWARE) {
                return c == sep;
            } else {
                int nextsep;
                if (c == sep) {
                    if (ON_DOS && len > 4 && filename.charAt(1) == sep) {
                        nextsep = filename.indexOf(sep, 2);
                        return nextsep > 2 && nextsep + 1 < len;
                    } else {
                        return false;
                    }
                } else {
                    nextsep = filename.indexOf(58);
                    return Character.isLetter(c) && nextsep == 1 && filename.length() > 2 && filename.charAt(2) == sep || ON_NETWARE && nextsep > 0;
                }
            }
        }
    }

    private TaskSessionUtils() {
    }
}
