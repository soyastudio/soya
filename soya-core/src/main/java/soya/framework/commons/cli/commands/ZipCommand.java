package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@Command(name = "gzip", uri = "resource://gzip")
public class ZipCommand extends ResourceCommand {
    @Override
    public String call() throws Exception {
        if ((contents() == null) || (contents().length() == 0)) {
            throw new IllegalArgumentException("Cannot zip null or empty string");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(contents().getBytes(Charset.defaultCharset()));
            }

            byte[] encoded = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
            return new String(encoded);
        } catch (IOException e) {
            throw new RuntimeException("Failed to zip content", e);
        }
    }
}
