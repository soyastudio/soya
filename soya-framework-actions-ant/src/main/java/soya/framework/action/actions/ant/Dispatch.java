package soya.framework.action.actions.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import soya.framework.action.*;

import java.util.List;
import java.util.Map;

public class Dispatch extends AntTaskExtension {

    private String name;
    private String uri;
    private Commandline commandline = new Commandline();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Commandline.Argument createArg() {
        return commandline.createArgument();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void execute() throws BuildException {
        try {
            ActionSignature.Builder builder = ActionSignature.builder(uri);
            String[] args = commandline.getArguments();
            StringBuilder queryBuilder = new StringBuilder();
            for(String arg : args) {
                if(arg.startsWith("--")) {
                    queryBuilder.append(arg.substring(2)).append("=");

                } else if(arg.startsWith("-")) {
                    queryBuilder.append(arg.substring(2)).append("=");

                } else {
                    queryBuilder.append(arg).append("&");

                }
            }

            if(queryBuilder.charAt(queryBuilder.length() - 1) == '&') {
                queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            }

            Map<String, List<String>> params = URIParser.splitQuery(queryBuilder.toString());
            params.entrySet().forEach(e -> {
                builder.set(e.getKey(), e.getValue().get(0));
            });

            ActionSignature signature = builder.create();
            ActionClass actionClass = ActionClass.get(signature.getActionName());



            /*

            Class<? extends ActionCallable> cls = ActionContext.getInstance().getActionType(ActionName.fromURI(uri));
            ActionCallable task = ActionParser.create(cls, commandline.getArguments());

            ActionResult result = task.call();
            if (name != null) {
                getProject().setResult(name, result.result());
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
