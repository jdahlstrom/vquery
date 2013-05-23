/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.johannesd.vquery;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class Util {

    public static <T> Set<T> set() {
        return new LinkedHashSet<T>();
    }

    public static <T> Set<T> set(Iterable<? extends T> cs) {
        if (cs instanceof Collection<?>) {
            return new LinkedHashSet<T>((Collection<T>) cs);
        } else {
            return addAll(new LinkedHashSet<T>(), cs);
        }
    }

    public static <T> Set<T> addAll(Set<T> set, Iterable<? extends T> iterable) {
        for (T t : iterable) {
            set.add(t);
        }
        return set;
    }

    public static <T, U> U cast(T instance, Class<U> klass) {
        if (klass.isAssignableFrom(instance.getClass())) {
            return klass.cast(instance);
        } else {
            return null;
        }
    }
}