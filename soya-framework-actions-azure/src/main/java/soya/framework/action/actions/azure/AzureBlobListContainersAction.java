package soya.framework.action.actions.azure;

import soya.framework.action.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command(group = "azure-blob-storage", name = "containers", httpMethod = Command.HttpMethod.GET)
public class AzureBlobListContainersAction extends AzureBlobAction<String[]>{

    @Override
    protected String[] execute() throws Exception {

        List<String> list = new ArrayList<>();
        blobServiceClient().listBlobContainers().forEach(e -> {
            list.add(e.getName());
        });
        Collections.sort(list);

        return list.toArray(new String[list.size()]);
    }
}
