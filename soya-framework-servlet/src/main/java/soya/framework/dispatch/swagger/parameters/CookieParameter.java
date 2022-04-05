package soya.framework.dispatch.swagger.parameters;

public class CookieParameter extends AbstractSerializableParameter<CookieParameter> {

    public CookieParameter() {
        super.setIn("cookie");
    }
}
