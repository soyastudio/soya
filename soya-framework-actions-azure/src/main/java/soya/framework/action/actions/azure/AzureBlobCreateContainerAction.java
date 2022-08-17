package soya.framework.action.actions.azure;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "azure-blob-storage", name = "create-container", httpMethod = Command.HttpMethod.POST)
public class AzureBlobCreateContainerAction extends AzureBlobAction<Boolean> {

    @CommandOption(option = "c", required = true)
    private String name;

    @Override
    protected Boolean execute() throws Exception {
        blobServiceClient().createBlobContainerIfNotExists(name);
        return blobServiceClient().getBlobContainerClient(name).exists();
    }
}
