package soya.framework.commons.reflect.cloning;

import soya.framework.commons.reflect.objenesis.instantiator.ObjectInstantiator;

/**
 * @author kostantinos.kougios
 *
 * 17 Jul 2012
 */
public interface IInstantiationStrategy
{
	<T> T newInstance(final Class<T> c);
	<T> ObjectInstantiator<T> getInstantiatorOf(Class<T> c);
}
