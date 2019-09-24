/**
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package soya.framework.commons.reflect.objenesis.strategy;

import soya.framework.commons.reflect.objenesis.ObjenesisException;
import soya.framework.commons.reflect.objenesis.instantiator.ObjectInstantiator;
import soya.framework.commons.reflect.objenesis.instantiator.android.AndroidSerializationInstantiator;
import soya.framework.commons.reflect.objenesis.instantiator.basic.ObjectStreamClassInstantiator;
import soya.framework.commons.reflect.objenesis.instantiator.gcj.GCJSerializationInstantiator;
import soya.framework.commons.reflect.objenesis.instantiator.perc.PercSerializationInstantiator;

import java.io.NotSerializableException;
import java.io.Serializable;

/**
 * Guess the best serializing instantiator for a given class. The returned instantiator will
 * instantiate classes like the genuine java serialization framework (the constructor of the first
 * not serializable class will be called). Currently, the selection doesn't depend on the class. It
 * relies on the
 * <ul>
 * <li>JVM version</li>
 * <li>JVM vendor</li>
 * <li>JVM vendor version</li>
 * </ul>
 * However, instantiators are stateful and so dedicated to their class.
 * 
 * @author Henri Tremblay
 * @see ObjectInstantiator
 */
public class SerializingInstantiatorStrategy extends BaseInstantiatorStrategy {

   /**
    * Return an {@link ObjectInstantiator} allowing to create instance following the java
    * serialization framework specifications.
    * 
    * @param type Class to instantiate
    * @return The ObjectInstantiator for the class
    */
   public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
      if(!Serializable.class.isAssignableFrom(type)) {
         throw new ObjenesisException(new NotSerializableException(type+" not serializable"));
      }
      if(PlatformDescription.JVM_NAME.startsWith(PlatformDescription.SUN) || PlatformDescription.isThisJVM(PlatformDescription.OPENJDK)) {
         return new ObjectStreamClassInstantiator<T>(type);
      }
      else if(PlatformDescription.JVM_NAME.startsWith(PlatformDescription.DALVIK)) {
         return new AndroidSerializationInstantiator<T>(type);
      }
      else if(PlatformDescription.JVM_NAME.startsWith(PlatformDescription.GNU)) {
         return new GCJSerializationInstantiator<T>(type);
      }
      else if(PlatformDescription.JVM_NAME.startsWith(PlatformDescription.PERC)) {
         return new PercSerializationInstantiator<T>(type);
      }
      
      return new ObjectStreamClassInstantiator<T>(type);
   }

}
