package soya.framework.action.actions.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.ListBlobsOptions;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "azure-blob-storage", name = "delete-all-blobs", httpMethod = Command.HttpMethod.DELETE)
public class AzureBlobDeleteAllBlobsAction extends AzureBlobAction<Integer> {

    @CommandOption(option = "c", required = true)
    private String container;

    @CommandOption(option = "f")
    private String prefix;

    private int count;

    @Override
    protected Integer execute() throws Exception {
        BlobContainerClient containerClient = getBlobContainerClient(container);
        containerClient.listBlobs(new ListBlobsOptions().setPrefix(prefix), null).forEach(e -> {
            containerClient.getBlobClient(e.getName()).getBlockBlobClient().deleteIfExists();
            count ++;
        });

        return count;
    }
}
