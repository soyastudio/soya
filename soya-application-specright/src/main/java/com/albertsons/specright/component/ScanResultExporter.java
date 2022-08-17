package com.albertsons.specright.component;

import com.albertsons.specright.service.Configuration;
import com.albertsons.specright.service.Event;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.Subscriber;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Subscriber.ListenTo(Specright.EVENT_RESULT_EXPORT)
public class ScanResultExporter extends SpecrightComponent {
    private static Logger logger = Logger.getLogger(ScanResultExporter.class.getName());

    @Override
    protected void process(Event event) throws Exception {
        byte[] contents = (byte[]) event.getPayload();
        String scanner = event.getParameter(SCANNER);
        String fileName = Configuration.get(Configuration.AZURE_BLOB_STORAGE_BASE_DIR) + "/" + scanner + "/" + scanner + "_" + System.currentTimeMillis() + ".gz";
        if(debug()) {
            logger.info("Sending message to: " + fileName);
        }
        azureService.writeBlobFile(contents, Configuration.get(Configuration.AZURE_BLOB_STORAGE_CONTAINER_NAME), fileName);
        specright.scanned(scanner);

        if(debug()) {
            logger.info("Message successfully sent to: " + fileName);
        }
    }
}
