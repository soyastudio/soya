package soya.framework.commandline;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

public abstract class Resource {

    public static final String SCHEMA_BASE64 = "base64";

    public static final String SCHEMA_GZIP = "gzip";

    public static final String SCHEMA_CLASSPATH = "classpath";

    public static final String SCHEMA_HOME = "home";

    public static final String SCHEMA_USER_HOME = "user-home";

    private final URI uri;

    protected Resource(URI uri) {
        this.uri = uri;
    }

    public static Resource create(String uri) {
        URI u = URI.create(uri);
        String schema = u.getScheme();

        if (SCHEMA_BASE64.equalsIgnoreCase(schema)) {
            return new Base64EncodedResource(u);

        } else if (SCHEMA_GZIP.equalsIgnoreCase(schema)) {
            return new GZipResource(u);

        } /*else if (SCHEMA_HOME.equalsIgnoreCase(schema)) {

            File dir = new File(System.getProperty("soya.home"));
            if (!dir.exists()) {
                dir = new File(new File(System.getProperty("user.home")), "soya");
            }

            if (!dir.exists()) {
                throw new IllegalArgumentException("Cannot locate home dir");
            }

            File file = new File(dir, host + u.getPath());

            return new FileResource(file);

        }*/ else if (SCHEMA_USER_HOME.equals(schema)) {
            return new FileResource(URI.create(new StringBuilder("file:///")
                    .append(System.getProperty("user.home").replaceAll("\\\\", "/"))
                    .append(uri.substring(SCHEMA_USER_HOME.length() + 2)).toString()));

        } else if (SCHEMA_CLASSPATH.equalsIgnoreCase(schema)) {
            return new ClasspathResource(u);

        } else {
            try {
                return new URLResource(u);

            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

    public URI uri() {
        return uri;
    }

    public String getAsString() throws IOException {
        return getAsString(Charset.defaultCharset());
    }

    public abstract String getAsString(Charset encoding) throws IOException;

    public abstract InputStream getAsInputStream() throws IOException;

    public abstract byte[] getAsByteArray() throws IOException;

    static class Base64EncodedResource extends Resource {
        private final String raw;

        protected Base64EncodedResource(URI uri) {
            super(uri);
            this.raw = uri.getHost();
        }

        @Override
        public String getAsString(Charset encoding) throws IOException {
            return new String(getAsByteArray(), encoding);
        }

        @Override
        public InputStream getAsInputStream() throws IOException {
            return new ByteArrayInputStream(getAsByteArray());
        }

        @Override
        public byte[] getAsByteArray() throws IOException {
            return Base64.getDecoder().decode(raw);
        }
    }

    static class GZipResource extends Resource {
        private final String raw;

        protected GZipResource(URI uri) {
            super(uri);
            this.raw = uri.getHost();
        }

        @Override
        public String getAsString(Charset encoding) throws IOException {
            byte[] compressed = Base64.getDecoder().decode(raw);
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
                try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                    try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, encoding)) {
                        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                            StringBuilder output = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                output.append(line);
                            }
                            return output.toString();
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to unzip content", e);
            }
        }

        @Override
        public InputStream getAsInputStream() throws IOException {
            byte[] compressed = Base64.getDecoder().decode(raw);
            return new GZIPInputStream(new ByteArrayInputStream(compressed));
        }

        @Override
        public byte[] getAsByteArray() throws IOException {
            byte[] compressed = Base64.getDecoder().decode(raw);
            InputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(compressed));
            ByteArrayOutputStream target = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int length;
            while ((length = inputStream.read(buf)) > 0) {
                target.write(buf, 0, length);
            }

            return target.toByteArray();
        }
    }

    static class FileResource extends Resource {
        private File file;

        protected FileResource(URI uri) {
            super(uri);
            this.file = Paths.get(uri).toFile();
        }

        @Override
        public String getAsString(Charset encoding) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(file.toURI()), encoding);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) {
                    builder.append("\n");
                }
                builder.append(lines.get(i));
            }
            return builder.toString();
        }

        @Override
        public InputStream getAsInputStream() throws IOException {
            return new FileInputStream(file);
        }

        @Override
        public byte[] getAsByteArray() throws IOException {
            return Files.readAllBytes(file.toPath());
        }
    }

    static class ClasspathResource extends Resource {
        private String path;

        protected ClasspathResource(URI uri) {
            super(uri);
            this.path = uri.toString().substring(SCHEMA_CLASSPATH.length() + 3);
        }

        @Override
        public String getAsString(Charset encoding) throws IOException {
            return null;
        }

        @Override
        public InputStream getAsInputStream() throws IOException {
            return null;
        }

        @Override
        public byte[] getAsByteArray() throws IOException {
            return new byte[0];
        }
    }

    static class URLResource extends Resource {
        private final URL url;

        protected URLResource(URI uri) throws MalformedURLException {
            super(uri);
            this.url = uri.toURL();
        }

        @Override
        public String getAsString(Charset encoding) throws IOException {
            return new Scanner(url.openStream(), encoding.toString()).useDelimiter("\\A").next();
        }

        @Override
        public InputStream getAsInputStream() throws IOException {
            return url.openStream();
        }

        @Override
        public byte[] getAsByteArray() throws IOException {
            return new byte[0];
        }
    }

}
