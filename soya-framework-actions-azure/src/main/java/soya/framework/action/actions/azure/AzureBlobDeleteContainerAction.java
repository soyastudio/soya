package soya.framework.action.actions.azure;

import soya.framework.action.Command;
import soya.framework.action.CommandOption;

@Command(group = "azure-blob-storage", name = "delete-container", httpMethod = Command.HttpMethod.DELETE)
public class AzureBlobDeleteContainerAction extends AzureBlobAction<Boolean> {

    @CommandOption(option = "c", required = true)
    private String name;

    @Override
    protected Boolean execute() throws Exception {
        blobServiceClient().deleteBlobContainerIfExists(name);
        return !blobServiceClient().getBlobContainerClient(name).exists();
    }
}
