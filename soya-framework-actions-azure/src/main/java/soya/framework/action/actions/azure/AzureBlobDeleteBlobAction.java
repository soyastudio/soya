package soya.framework.action.actions.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "azure-blob-storage", name = "delete-blob", httpMethod = Command.HttpMethod.DELETE)
public class AzureBlobDeleteBlobAction extends AzureBlobAction<Boolean> {


    @CommandOption(option = "c", required = true)
    private String container;

    @CommandOption(option = "f", required = true)
    private String filename;

    @Override
    protected Boolean execute() throws Exception {
        BlobContainerClient containerClient = getBlobContainerClient(container);
        BlockBlobClient blockBlobClient = containerClient.getBlobClient(filename).getBlockBlobClient();
        return blockBlobClient.deleteIfExists();
    }
}
