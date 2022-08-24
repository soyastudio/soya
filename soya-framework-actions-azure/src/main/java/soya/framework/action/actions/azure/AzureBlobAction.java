package soya.framework.action.actions.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import soya.framework.action.Action;
import soya.framework.action.ActionContext;
import soya.framework.action.Domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

@Domain(group = "azure-blob-storage", title = "Azure Blob Storage Service", description = "Azure Blob Storage Service")
public abstract class AzureBlobAction<T> extends Action<T> {

    protected BlobServiceClient blobServiceClient() {
        return ActionContext.getInstance().getService(BlobServiceClient.class);
    }

    protected BlobContainerClient getBlobContainerClient(String containerName) {
        BlobContainerClient client = blobServiceClient().getBlobContainerClient(containerName);
        if (!client.exists()) {
            client.create();
        }

        return client;
    }

    protected byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gzipOutputStream.write(data);

        return byteArrayOutputStream.toByteArray();
    }
}
