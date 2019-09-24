package soya.framework.commons.reflect.cloning;

import soya.framework.commons.reflect.objenesis.Objenesis;
import soya.framework.commons.reflect.objenesis.ObjenesisStd;
import soya.framework.commons.reflect.objenesis.instantiator.ObjectInstantiator;

/**
 * @author kostantinos.kougios
 *
 * 17 Jul 2012
 */
public class ObjenesisInstantiationStrategy implements IInstantiationStrategy
{
	private final Objenesis objenesis	= new ObjenesisStd();

	public <T> T newInstance(Class<T> c)
	{
		return objenesis.newInstance(c);
	}

	public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> c) {
		return objenesis.getInstantiatorOf(c);
	}

	private static ObjenesisInstantiationStrategy instance	= new ObjenesisInstantiationStrategy();

	public static ObjenesisInstantiationStrategy getInstance()
	{
		return instance;
	}
}
