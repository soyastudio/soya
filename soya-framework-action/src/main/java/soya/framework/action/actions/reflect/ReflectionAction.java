package soya.framework.action.actions.reflect;

import soya.framework.action.Action;
import soya.framework.action.Domain;

@Domain(group = "reflect", title = "Reflection Commands", description = "Commands for reflecting system runtime information and command metadata.")
public abstract class ReflectionAction<T> extends Action<T> {

}
