package soya.framework.support;

import com.google.gson.JsonElement;
import soya.framework.JsonCompatible;

public interface GsonCompatible extends JsonCompatible {
    JsonElement getAsJsonElement();
}
