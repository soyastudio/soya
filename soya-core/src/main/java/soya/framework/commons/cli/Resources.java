package soya.framework.commons.cli;

import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.xml.sax.InputSource;
import soya.framework.commons.io.IOUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Resources {

    private static final String regex = "\\$\\{([A-Za-z_.][A-Za-z0-9_.]*)}";
    private static final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

    public enum ResourceType {
        URL, FILE, BASE64, GZIPPED, PLAIN;
    }

    private Resources() {
    }

    public static String get(String source) throws Exception {
        ResourceType type = guessType(source);

        switch (type) {
            case FILE:
                return fromFile(source);

            case URL:
                return fromUrl(source);

            case BASE64:
                return decode(source);

            default:
                return source;
        }
    }

    protected static String fromFile(String path) throws IOException {
        return IOUtils.toString(new FileInputStream(path));
    }

    protected static String fromUrl(String url) throws IOException {
        return IOUtils.toString(new URL(url).openStream());
    }

    protected static String decode(String src) throws Exception {
        byte[] decoded = Base64.decodeBase64(src);
        if(isGZipped(decoded)) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decoded)) {
                try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                    try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
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
        } else {
            return new String(decoded);
        }
    }

    public static ResourceType guessType(String source) {
        if (isFile(source)) {
            return ResourceType.FILE;

        } else if (isURL(source)) {
            return ResourceType.URL;

        } else if (isBas64Encoded(source)) {
            return ResourceType.BASE64;

        } else {
            return ResourceType.PLAIN;
        }
    }

    public static boolean isFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    public static boolean isURL(String source) {
        try {
            new URL(source);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isJson(String source) {
        String token = source.trim();
        if (token.startsWith("[") && token.endsWith("]")
                || token.startsWith("{") && token.endsWith("}")) {
            try {
                JsonParser.parseString(source);
                return true;

            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isXml(String source) {
        try {
            DocumentBuilderFactory
                    .newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(source)));
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBas64Encoded(String source) {
        return Base64.isBase64(source);
    }

    public static boolean isGZipped(String source) {
        byte[] compressed = source.getBytes(StandardCharsets.UTF_8);
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public static boolean isGZipped(byte[] compressed) {
         return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public static String getFileExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    public static void compile(Properties properties) {

        int num = properties.size();
        while (true) {
            Properties values = new Properties();
            properties.entrySet().forEach(e -> {
                String v = (String) e.getValue();
                if (!v.contains("${")) {
                    values.setProperty((String) e.getKey(), v);
                }
            });

            if (values.size() == num) {
                break;

            } else {
                num = values.size();

            }

            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);

                if (value.contains("${")) {
                    if (value.contains("${" + key + "}")) {
                        throw new IllegalArgumentException("Self referenced for " + "${" + key + "}");
                    }

                    value = StrSubstitutor.replace(value, values);

                    if (value.contains("${")) {
                        value = StrSubstitutor.replace(value, System.getProperties());
                    }

                    properties.setProperty(key, value);

                }
            }
        }
    }

    public static String evaluate(String exp, Properties properties) {

        String expression = exp;

        if (expression != null && expression.contains("${")) {
            StringBuffer buffer = new StringBuffer();
            Matcher matcher = pattern.matcher(expression);
            while (matcher.find()) {
                String token = matcher.group(1);
                String value = token;
                if (properties.getProperty(token) != null) {
                    value = properties.getProperty(token);

                } else if (System.getProperty(token) != null) {
                    value = System.getProperty(token);
                }
                matcher.appendReplacement(buffer, value);
            }
            matcher.appendTail(buffer);
            expression = buffer.toString();
        }

        return expression;
    }


}
