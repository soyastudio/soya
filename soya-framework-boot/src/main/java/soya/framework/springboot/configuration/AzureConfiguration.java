package soya.framework.springboot.configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AzureConfiguration {
    private static final String ACCOUNT_NAME = "spring.cloud.azure.storage.blob.account-name";
    private static final String ACCOUNT_KEY = "spring.cloud.azure.storage.blob.account-key";
    private static final String ENDPOINT = "spring.cloud.azure.storage.blob.endpoint";

    @Autowired
    Environment environment;

    @Bean
    BlobServiceClient blobServiceClient() {
        StringBuilder builder = new StringBuilder()
                .append("DefaultEndpointsProtocol=https;")
                .append("AccountName=").append(environment.getProperty(ACCOUNT_NAME)).append(";")
                .append("AccountKey=").append(environment.getProperty(ACCOUNT_KEY)).append(";");

        return new BlobServiceClientBuilder()
                .connectionString(builder.toString())
                .endpoint(environment.getProperty(ENDPOINT)).buildClient();
    }

}
