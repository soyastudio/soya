package soya.framework.commands;

import soya.framework.core.CommandRunner;

import java.net.URI;

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
