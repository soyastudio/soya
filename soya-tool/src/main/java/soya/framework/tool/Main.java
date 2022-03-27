package soya.framework.tool;

import org.reflections.Reflections;
import soya.framework.commons.cli.Command;
import soya.framework.commons.cli.CommandCallable;
import soya.framework.commons.cli.CommandRunner;

import java.net.URI;
import java.util.Set;

public class Main extends CommandRunner {

    public static void main(String[] args) {
       try {
            URI uri = new URI("class://soya.framework.tool.codegen.rest.RestApiGenerator?g=resource&p=soya.framework.albertsons.restapi");

            Object result = execute(uri);
            if (result != null) {
                System.out.println(result);
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
