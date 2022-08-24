package soya.framework.action.actions.http.okhttp;

import okhttp3.Response;
import soya.framework.action.Command;

@Command(group = "http-client", name = "okhttp-get", httpMethod = Command.HttpMethod.POST)
public class OkHttpGetAction extends OkHttpAction {

    @Override
    protected Response execute() throws Exception {


        return null;
    }
}
