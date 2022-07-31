package com.albertsons.specright.service;

import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeoutException;

public abstract class Specright {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String EVENT_HEARTBEAT = "specright://heartbeat";
    public static final String EVENT_JOB_TRACKING = "specright://job-tracking";
    public static final String EVENT_RESULT_EXPORT = "specright://export";
    public static final String EVENT_Exception_HANDLE = "specright://exception-handler";

    private static Specright me;

    private PostmanEnvironment environment;
    private PostmanCollection collection;

    protected long heartbeatDelay = 10000l;
    protected long heartbeatPeriod = 10000l;

    protected Specright() {
        if (me != null) {
            throw new IllegalStateException("Instance already exist");
        }

        me = this;
    }

    protected void configure(PostmanEnvironment environment, PostmanCollection collection) {
        this.environment = environment;
        this.collection = collection;

        if (environment.get("heartbeatDelay") != null) {
            this.heartbeatPeriod = Long.parseLong(environment.get("heartbeatDelay"));
        }

        if (environment.get("heartbeatPeriod") != null) {
            this.heartbeatPeriod = Long.parseLong(environment.get("heartbeatPeriod"));
        }

    }

    public Set<String> scanners() {
        return collection.requests.keySet();
    }

    public boolean debug() {
        return environment.get("debug") == null ? false : Boolean.parseBoolean(environment.get("debug"));
    }

    // OkHttp Call
    public Token fetchToken() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(environment.get("username"), environment.get("password")))
                .build();

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), "{\"grant_type\": \"password\"}");

        Request request = new Request.Builder()
                .url(environment.get("authHost") + "/token")
                .addHeader("Authorization", Credentials.basic("username", "password"))
                .post(body)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        return GSON.fromJson(response.body().charStream(), Token.class);
    }

    public String bulkJob(String scanner, Token token) throws IOException {

        PostmanCollection.Request model = collection.requests.get(scanner);

        String url = model.url.raw;
        url = url.replace("{{host}}", environment.get("host"));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token.getAccessToken())
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        RequestBody body = RequestBody.create(null, new byte[]{});

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-api-key", environment.get("apiKey"))
                .addHeader("x-user-id", environment.get("userId"))
                .post(body)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();

        if (response.code() == 202) {
            return JsonParser.parseReader(response.body().charStream()).getAsJsonObject().get("job-id").getAsString();

        } else {
            throw new RuntimeException();
        }

    }

    public byte[] trackJob(String jobId, String token) throws IOException, TimeoutException {
        long timestamp = System.currentTimeMillis();
        do {
            if(System.currentTimeMillis() - timestamp > 30000l) {
                throw new TimeoutException("Job cannot complete in 30 sec: " + jobId);
            }

            try {
                Thread.sleep(1500l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while ("COMPLETE".equalsIgnoreCase(jobStatus(jobId, token)));


        Request getDetails = new Request.Builder()
                .url(getJobDetailsUrl(jobId))
                .addHeader("x-api-key", environment.get("apiKey"))
                .addHeader("x-user-id", environment.get("userId"))
                .get()
                .build();


        Response response = authClient(token).newCall(getDetails).execute();

        return response.body().bytes();

    }

    public String jobStatus(String jobId, String token) throws IOException {

        Request getStatus = new Request.Builder()
                .url(getJobStatusUrl(jobId))
                .addHeader("x-api-key", environment.get("apiKey"))
                .addHeader("x-user-id", environment.get("userId"))
                .get()
                .build();

        Response response = authClient(token).newCall(getStatus).execute();
        if (response.code() / 100 == 2) {
            return JsonParser.parseReader(response.body().charStream()).getAsJsonObject().get("status").getAsString();

        } else {
            throw new RuntimeException();
        }

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
        return new StringBuilder(environment.get("host"))
                .append("/bulkjob/")
                .append(jobId)
                .append("/status?isQuery=true")
                .toString();
    }

    private String getJobDetailsUrl(String jobId) {
        return new StringBuilder(environment.get("host"))
                .append("/bulkjob/")
                .append(jobId)
                .append("/details?isQuery=true&includeLabels=false")
                .toString();
    }

    public static Specright getInstance() {
        return me;
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
