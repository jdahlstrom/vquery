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
import com.vaadin.ui.HasComponents;

public interface Map<From, To> {
    Set<To> apply(Set<? extends From> cs);

    public static final Map<Component, Component> children = new Map<Component, Component>() {
        @Override
        public Set<Component> apply(Set<? extends Component> cs) {
            Set<Component> result = Util.set();
            for (Component c : cs) {
                if (c instanceof HasComponents) {
                    Util.addAll(result, (HasComponents) c);
                }
            }
            return result;
        }
    };

    public static final Map<Component, HasComponents> parent = new Map<Component, HasComponents>() {
        @Override
        public Set<HasComponents> apply(Set<? extends Component> cs) {
            Set<HasComponents> result = Util.set();
            for (Component c : cs) {
                if (c.getParent() != null) {
                    result.add(c.getParent());
                }
            }
            return result;
        }
    };

    public static final Map<Component, Component> descendants = new Map<Component, Component>() {
        @Override
        public Set<Component> apply(Set<? extends Component> cs) {
            return doApply(children.apply(cs));
        }

        private Set<Component> doApply(Set<? extends Component> cs) {
            Set<Component> result = Util.set();
            for (Component c : cs) {
                result.add(c);
                if (c instanceof HasComponents) {
                    result.addAll(doApply(Util.set((HasComponents) c)));
                }
            }
            return result;
        }
    };

    public static final Map<Component, HasComponents> ancestors = new Map<Component, HasComponents>() {
        @Override
        public Set<HasComponents> apply(Set<? extends Component> cs) {
            Set<HasComponents> result = Util.set();
            for (Component c : cs) {
                HasComponents parent = c.getParent();
                while (parent != null) {
                    result.add(parent);
                    parent = parent.getParent();
                }
            }
            return result;
        }
    };
}
