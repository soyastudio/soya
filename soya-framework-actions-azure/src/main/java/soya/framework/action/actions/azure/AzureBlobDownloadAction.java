package soya.framework.action.actions.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;
import soya.framework.util.StreamUtils;

import java.io.InputStream;

@Command(group = "azure-blob-storage", name = "download", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_OCTET_STREAM})
public class AzureBlobDownloadAction extends AzureBlobAction<byte[]> {

    @CommandOption(option = "c", required = true)
    private String container;

    @CommandOption(option = "f")
    private String filename;

    @Override
    protected byte[] execute() throws Exception {
        BlobContainerClient containerClient = getBlobContainerClient(container);
        BlockBlobClient blob = containerClient.getBlobClient(filename).getBlockBlobClient();
        InputStream input = blob.openInputStream();

        return StreamUtils.copyToByteArray(input);
    }
}
