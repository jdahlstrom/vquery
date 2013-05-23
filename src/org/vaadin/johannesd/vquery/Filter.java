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

import java.util.Set;

import com.vaadin.ui.Component;

public interface Filter<T extends Component> {
    Set<T> apply(Set<T> cs);

    class ByPredicate<T extends Component> implements Filter<T> {
        private Predicate<T> predicate;

        public ByPredicate(Predicate<T> p) {
            predicate = p;
        }

        @Override
        public Set<T> apply(Set<T> cs) {
            Set<T> result = Util.set();
            for (T c : cs) {
                if (predicate.apply(c)) {
                    result.add(c);
                }
            }
            return result;
        }
    }
}
