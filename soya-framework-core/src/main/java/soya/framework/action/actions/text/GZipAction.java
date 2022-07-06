package soya.framework.action.actions.text;

import soya.framework.action.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@Command(group = "text-util", name = "gzip")
public class GZipAction extends TextUtilAction {

    @Override
    protected String execute() throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(source.getBytes(encoding));
            }

            byte[] encoded = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            return new String(encoded);

        } catch (IOException e) {
            throw new RuntimeException("Failed to zip content", e);
        }
    }
}
