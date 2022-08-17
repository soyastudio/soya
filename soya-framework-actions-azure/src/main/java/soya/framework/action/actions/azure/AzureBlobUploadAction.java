package soya.framework.action.actions.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Command(group = "azure-blob-storage", name = "upload", httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class AzureBlobUploadAction extends AzureBlobAction<Integer> {

    @CommandOption(option = "c", required = true)
    private String container;

    @CommandOption(option = "f")
    private String filename;

    @CommandOption(option = "d", dataForProcessing = true)
    private String contents;

    @Override
    protected Integer execute() throws Exception {
        byte[] raw = gzip(contents.getBytes(StandardCharsets.UTF_8));
        if(filename.toLowerCase().endsWith(".gz")) {
            raw = gzip(raw);
        }

        BlobContainerClient containerClient = getBlobContainerClient(container);
        BlockBlobClient blockBlobClient = containerClient.getBlobClient(filename).getBlockBlobClient();
        InputStream inputStream = new ByteArrayInputStream(raw);
        blockBlobClient.upload(inputStream, raw.length, true);

        return raw.length;
    }
}
