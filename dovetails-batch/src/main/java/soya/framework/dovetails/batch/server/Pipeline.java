package soya.framework.dovetails.batch.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Pipeline {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobParameters getJobParameters() {
        Map<String, JobParameter> params = new LinkedHashMap<>();
        params.put("name", new JobParameter(name, true));
        return new JobParameters(params);
    }

    public static Pipeline fromJson(File configFile) {
        Gson gson = new Gson();
        try {
            JsonElement jsonElement = new JsonParser().parse(new FileReader(configFile));
            return gson.fromJson(jsonElement, Pipeline.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
