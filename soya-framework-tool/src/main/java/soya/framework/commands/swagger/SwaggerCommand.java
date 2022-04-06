package soya.framework.commands.swagger;

import com.google.gson.GsonBuilder;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import soya.framework.commons.io.IOUtils;
import soya.framework.core.Command;
import soya.framework.core.CommandCallable;

import java.io.InputStream;

@Command(group = "swagger", name = "parser")
public class SwaggerCommand implements CommandCallable<String> {

    @Override
    public String call() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("swagger.yaml");

        Swagger swagger = new SwaggerParser().parse(IOUtils.toString(inputStream));

        swagger.getPaths().entrySet().forEach(e -> {
            Path path = e.getValue();
            path.getOperationMap().entrySet().forEach(o -> {

                Operation operation = o.getValue();
                System.out.println("--------- " + operation.getOperationId() + ": " + operation.getTags().size());
            });
        });


        return "";
    }
}
