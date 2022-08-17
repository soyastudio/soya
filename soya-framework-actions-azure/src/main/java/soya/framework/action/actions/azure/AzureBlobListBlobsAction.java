package soya.framework.action.actions.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.ListBlobsOptions;
import soya.framework.action.Command;
import soya.framework.action.CommandOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "azure-blob-storage", name = "list-blobs", httpMethod = Command.HttpMethod.GET)
public class AzureBlobListBlobsAction extends AzureBlobAction<String[]> {

    @CommandOption(option = "c", required = true)
    private String container;

    @CommandOption(option = "f")
    private String prefix;

    @Override
    protected String[] execute() throws Exception {

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

        return list.toArray(new String[list.size()]);
    }
}
