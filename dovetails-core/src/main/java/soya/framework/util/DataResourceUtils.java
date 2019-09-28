package soya.framework.util;

import com.google.common.io.Files;
import soya.framework.DataObject;
import soya.framework.support.JsonData;
import soya.framework.support.PlainTextData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataResourceUtils {
    private DataResourceUtils() {
    }

    public static DataObject fromClasspath(String path, ClassLoader classLoader) throws IOException {
        DataObject result = null;
        String extension = Files.getFileExtension(path);
        InputStream is = classLoader.getResourceAsStream(path);
        if("json".equalsIgnoreCase(extension)) {
            result = JsonData.fromeJson(new InputStreamReader(is));

        } else {
            result = PlainTextData.builder().fromResource(path, classLoader);
        }

        return result;
    }
}
