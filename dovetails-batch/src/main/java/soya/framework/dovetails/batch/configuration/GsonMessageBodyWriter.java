package soya.framework.dovetails.batch.configuration;

import com.google.gson.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonMessageBodyWriter implements MessageBodyWriter<Object>,
        MessageBodyReader<Object> {

    private static final String UTF_8 = "UTF-8";

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              java.lang.annotation.Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType,
                           Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException {
        InputStreamReader streamReader = new InputStreamReader(entityStream,
                UTF_8);
        try {
            return GsonUtil.getInstance().fromJson(streamReader, genericType);
        } catch (com.google.gson.JsonSyntaxException e) {
            // Log exception
        } finally {
            streamReader.close();
        }
        return null;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object object, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException,
            WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
        try {
            GsonUtil.getInstance().toJson(object, genericType, writer);
        } finally {
            writer.close();
        }
    }

    static class GsonUtil {
        public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";

        private static Gson gson;
        private static Gson gsonExpose;
        private static SimpleDateFormat sdf;

        public static Gson getInstance() {
            if (gson == null) {
                gson = getGsonBuilderInstance(false).create();
            }
            return gson;
        }

        public static Gson getExposeInstance() {
            if (gsonExpose == null) {
                gsonExpose = getGsonBuilderInstance(true).create();
            }
            return gsonExpose;
        }

        public static Gson getInstance(boolean onlyExpose) {
            if (!onlyExpose) {
                if (gson == null) {
                    gson = getGsonBuilderInstance(false).create();
                }
                return gson;
            } else {
                if (gsonExpose == null) {
                    gsonExpose = getGsonBuilderInstance(true).create();
                }
                return gsonExpose;
            }
        }

        public static SimpleDateFormat getSDFInstance() {
            if (sdf == null) {
                sdf = new SimpleDateFormat(PATTERN);
            }
            return sdf;
        }

        private static GsonBuilder getGsonBuilderInstance(boolean onlyExpose) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            if (onlyExpose) {
                gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            }
            gsonBuilder.registerTypeAdapter(Date.class,
                    new JsonDeserializer<Date>() {
                        @Override
                        public Date deserialize(JsonElement json, Type type,
                                                JsonDeserializationContext arg2)
                                throws JsonParseException {
                            try {
                                return getSDFInstance().parse(json.getAsString());
                            } catch (ParseException e) {
                                return null;
                            }
                        }

                    });
            gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                @Override
                public JsonElement serialize(Date src, Type typeOfSrc,
                                             JsonSerializationContext context) {
                    return src == null ? null : new JsonPrimitive(getSDFInstance()
                            .format(src));
                }
            });
            return gsonBuilder;
        }

        public static <T> T fromJson(String json, Class<T> classOfT,
                                     boolean onlyExpose) {
            try {
                return getInstance(onlyExpose).fromJson(json, classOfT);
            } catch (Exception ex) {
                // Log exception
                return null;
            }
        }
    }
}
