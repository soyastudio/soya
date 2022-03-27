package soya.framework.tool.js;

import soya.framework.commons.cli.Flow;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptCallback implements Flow.Callback {

    @Override
    public void onSuccess(Flow.Session session) throws Exception {
        String script = "print ('Hello World');";

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
        try {
            jsEngine.eval(script);
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
    }
}
