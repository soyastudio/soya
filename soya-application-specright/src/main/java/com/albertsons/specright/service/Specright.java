package com.albertsons.specright.service;

import com.google.gson.*;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public abstract class Specright {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String EVENT_HEARTBEAT = "specright://heartbeat";
    public static final String EVENT_JOB_TRACKING = "specright://job-tracking";
    public static final String EVENT_RESULT_EXPORT = "specright://export";
    public static final String EVENT_Exception_HANDLE = "specright://exception-handler";

    protected PostmanCollection collection;
    protected int sequence;
    protected Map<String, Long> lastScannedTimestamps = new LinkedHashMap<>();

    protected Specright() {
    }

    protected void configure(PostmanCollection collection) {
        this.collection = collection;
    }

    public Set<String> scanners() {
        return collection.requests.keySet();
    }

    public int getSequence() {
        return sequence;
    }

    public void scanned(String scanner) {
        lastScannedTimestamps.put(scanner, System.currentTimeMillis());
    }

    public Long getLastScannedTimestamp(String scanner) {
        return lastScannedTimestamps.get(scanner);
    }

    // ===================================== OkHttp Call
    public String authenticate() throws SpecrightException {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(Configuration.get(Configuration.SPECRIGHT_USERNAME), Configuration.get(Configuration.SPECRIGHT_PASSWORD)))
                .build();

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), "{\"grant_type\": \"password\"}");

        Request request = new Request.Builder()
                .url(Configuration.get(Configuration.SPECRIGHT_AUTH_HOST) + "/token")
                .post(body)
                .build();

        return toJson(execute(client.newCall(request)));
    }

    public String fetchToken() throws SpecrightException {
        return getProperty("access_token", authenticate());
    }

    public String scan(String scanner, String token) throws SpecrightException {

        PostmanCollection.Request model = collection.requests.get(scanner);

        String url = model.url.raw;
        url = url.replace("{{host}}", Configuration.get(Configuration.SPECRIGHT_HOST));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        RequestBody body = RequestBody.create(null, new byte[]{});

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", Configuration.get(Configuration.SPECRIGHT_API_KEY))
                .addHeader("x-user-id", Configuration.get(Configuration.SPECRIGHT_USER_ID))
                .post(body)
                .build();

        return getProperty("job-id", bulkJob(scanner, token));

    }

    public String bulkJob(String scanner, String token) throws SpecrightException {

        PostmanCollection.Request model = collection.requests.get(scanner);

        String url = model.url.raw;
        url = url.replace("{{host}}", Configuration.get(Configuration.SPECRIGHT_HOST));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        RequestBody body = RequestBody.create(null, new byte[]{});

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", Configuration.get(Configuration.SPECRIGHT_API_KEY))
                .addHeader("x-user-id", Configuration.get(Configuration.SPECRIGHT_USER_ID))
                .post(body)
                .build();

        return toJson(execute(client.newCall(request)));

    }

    public String jobStatus(String jobId, String token) throws SpecrightException {

        Request getStatus = new Request.Builder()
                .url(getJobStatusUrl(jobId))
                .addHeader("x-api-key", Configuration.get(Configuration.SPECRIGHT_API_KEY))
                .addHeader("x-user-id", Configuration.get(Configuration.SPECRIGHT_USER_ID))
                .get()
                .build();

        return toJson(execute(authClient(token).newCall(getStatus)));

    }

    public byte[] jobDetails(String jobId, String token) throws SpecrightException {
        Request getDetails = new Request.Builder()
                .url(getJobDetailsUrl(jobId))
                .addHeader("x-api-key", Configuration.get(Configuration.SPECRIGHT_API_KEY))
                .addHeader("x-user-id", Configuration.get(Configuration.SPECRIGHT_USER_ID))
                .get()
                .build();


        Response response = execute(authClient(token).newCall(getDetails));

        try {
            return response.body().bytes();
        } catch (IOException e) {
            throw new SpecrightException(e);
        }
    }

    public byte[] gzip(byte[] data) throws SpecrightException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(data);
            }

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new SpecrightException(e);
        }
    }

    public byte[] csvFilter(String scanner, byte[] data) {
        if(data == null && data.length == 0) {
            return new byte[0];
        }

        if ("Supplier".equalsIgnoreCase(scanner)) {
            CsvDynaClass csv = new CsvDynaClass(scanner, data);
            csv.include(bean -> {
                if ("Supplier".equalsIgnoreCase((String) bean.get("Record_Type_for_Rule__c"))) {
                    return true;
                }

                return false;
            });

            return csv.toCSV().getBytes(StandardCharsets.UTF_8);

        } else if("Facility".equalsIgnoreCase(scanner)) {
            CsvDynaClass csv = new CsvDynaClass(scanner, data);
            csv.include(bean -> {
                if ("Manufacturing Facility".equalsIgnoreCase((String) bean.get("Record_Type_for_Rule__c"))) {
                    return true;
                }

                return false;
            });

            return csv.toCSV().getBytes(StandardCharsets.UTF_8);

        }


        return data;
    }

    public byte[] filterByLastUpdated(String scanner, byte[] data) {
        System.out.println("================= filterByLastUpdated...");

        return data;
    }

    private OkHttpClient authClient(String token) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

    }

    private String getJobStatusUrl(String jobId) {
        return new StringBuilder(Configuration.get(Configuration.SPECRIGHT_HOST))
                .append("/bulkjob/")
                .append(jobId)
                .append("/status?isQuery=true")
                .toString();
    }

    private String getJobDetailsUrl(String jobId) {
        return new StringBuilder(Configuration.get(Configuration.SPECRIGHT_HOST))
                .append("/bulkjob/")
                .append(jobId)
                .append("/details?isQuery=true&includeLabels=false")
                .toString();
    }

    private Response execute(Call call) throws SpecrightException {
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new SpecrightException(e);
        }

        if (response.code() / 100 != 2) {
            throw new SpecrightException("Unexpected response: " + response.code());
        }

        return response;
    }

    private String toJson(Response response) {
        return GSON.toJson(JsonParser.parseReader(response.body().charStream()));
    }

    private String getProperty(String propName, String json) {
        return JsonParser.parseString(json).getAsJsonObject().get(propName).getAsString();
    }

    static class BasicAuthInterceptor implements Interceptor {

        private final String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

    public static class Token {
        private String access_token;
        private String token_type;
        private long expires_in;
        private String refresh_token;

        public String getAccessToken() {
            return access_token;
        }

        public String getTokenType() {
            return token_type;
        }

        public long getExpiresIn() {
            return expires_in;
        }

        public String getRefreshToken() {
            return refresh_token;
        }
    }

    public static class PostmanEnvironment {
        private Set<AbstractMap.SimpleEntry<String, String>> values = new HashSet();

        public Set<? extends Map.Entry<String, String>> entrySet() {
            return values;
        }

        public String get(String key) {
            Iterator<AbstractMap.SimpleEntry<String, String>> iterator = values.iterator();
            while (iterator.hasNext()) {
                AbstractMap.SimpleEntry<String, String> entry = iterator.next();
                if (entry.getKey().equals(key)) {
                    return entry.getValue();
                }
            }

            return null;
        }
    }

    public static class PostmanCollection {
        private Map<String, Request> requests = new HashMap<>();

        public static PostmanCollection fromInputStream(InputStream inputStream) {
            PostmanCollection collection = new PostmanCollection();
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();
            if (jsonObject.get("item") != null) {
                JsonArray array = jsonObject.get("item").getAsJsonArray();
                array.forEach(e -> {
                    if (e.isJsonObject() && e.getAsJsonObject().get("name").getAsString().equals("Bulk Jobs")) {
                        JsonArray arr = e.getAsJsonObject().get("item").getAsJsonArray();
                        load(arr, collection);
                    }
                });
            }

            return collection;
        }

        private static void load(JsonArray array, PostmanCollection collection) {
            array.forEach(e -> {
                JsonObject object = e.getAsJsonObject();
                if (object.get("request") != null) {
                    String name = object.get("name").getAsString();
                    Request request = GSON.fromJson(object.get("request"), Request.class);
                    collection.requests.put(name, request);

                }
            });
        }

        static class Request {
            private URL url;
            private String method;

        }

        static class URL {
            private String raw;
            private String protocol;
            private String[] host;
            private String[] path;
            private String port;

        }
    }
}
