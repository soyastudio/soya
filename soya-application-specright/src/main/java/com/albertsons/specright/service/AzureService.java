package com.albertsons.specright.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AzureService {

    @Autowired
    BlobServiceClient blobServiceClient;

    // ===================================== Azure Blob Storage
    public List<String> containerNames() {
        List<String> list = new ArrayList<>();
        blobServiceClient.listBlobContainers().forEach(e -> {
            list.add(e.getName());
        });
        Collections.sort(list);

        return list;
    }

    public void createContainer(String containerName) {
        blobServiceClient.createBlobContainerIfNotExists(containerName);
    }

    public void deleteContainer(String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        containerClient.deleteIfExists();
    }

    public List<String> listBlobs(String container, String prefix) {
        List<String> list = new ArrayList<>();
        BlobContainerClient containerClient = getBlobContainerClient(container);
        if (prefix == null || prefix.isEmpty()) {
            containerClient.listBlobs().forEach(e -> {
                list.add(e.getName());
            });
        } else {
            containerClient.listBlobs(new ListBlobsOptions().setPrefix(prefix), null).forEach(e -> {
                list.add(e.getName());
            });
        }
        Collections.sort(list);

        return list;
    }

    public byte[] readBlobFile(String container, String fileName) throws SpecrightException {

        try {
            BlobContainerClient containerClient = getBlobContainerClient(container);
            BlockBlobClient blob = containerClient.getBlobClient(fileName).getBlockBlobClient();
            InputStream input = blob.openInputStream();

            return StreamUtils.copyToByteArray(input);
        } catch (IOException e) {
            throw new SpecrightException(e);
        }
    }

    public Boolean writeBlobFile(byte[] data, String container, String fileName) throws SpecrightException {
        BlobContainerClient containerClient = getBlobContainerClient(container);
        BlockBlobClient blockBlobClient = containerClient.getBlobClient(fileName).getBlockBlobClient();
        InputStream inputStream = new ByteArrayInputStream(data);
        blockBlobClient.upload(inputStream, data.length, true);

        return blockBlobClient.exists();
    }

    public boolean deleteBlobFile(String container, String fileName) {
        BlobContainerClient containerClient = getBlobContainerClient(container);
        BlockBlobClient blockBlobClient = containerClient.getBlobClient(fileName).getBlockBlobClient();
        return blockBlobClient.deleteIfExists();
    }

    public void deleteAll(String container, String prefix) {
        BlobContainerClient containerClient = getBlobContainerClient(container);

        containerClient.listBlobs(new ListBlobsOptions().setPrefix(prefix), null).forEach(e -> {
            containerClient.getBlobClient(e.getName()).getBlockBlobClient().deleteIfExists();
        });
    }

    private BlobContainerClient getBlobContainerClient(String containerName) {
        BlobContainerClient client = blobServiceClient.getBlobContainerClient(containerName);
        if (!client.exists()) {
            client.create();
        }

        return client;
    }
}
