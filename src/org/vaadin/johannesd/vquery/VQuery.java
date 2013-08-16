/*
 * Copyright 2013 Johannes Dahlström.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;

public class VQuery {

    public static Query<Component> $() {
        return all();
    }

    public static <C extends Component> Query<C> $(C... cs) {
        return VQuery.select(Arrays.asList(cs));
    }

    public static <C extends Component> Query<C> $(Collection<C> cs) {
        return VQuery.select(cs);
    }

    public static FieldQuery<Field<?>> $(Field<?>... fields) {
        return VQuery.select(Arrays.asList(fields));
    }

    public static FieldQuery<Field<?>> $(Collection<Field<?>> fields) {
        return VQuery.select(fields);
    }

    public static Query<Component> all() {
        if (UI.getCurrent() == null) {
            throw new IllegalStateException("UI.getCurrent() is null");
        }
        Query<Component> ui = $((Component) UI.getCurrent());
        return ui.descendants().with(ui);
    }

    public static <C extends Component> Query<C> select(C... cs) {
        return VQuery.select(Arrays.asList(cs));
    }

    public static <C extends Component> Query<C> select(Collection<C> cs) {
        return new Query<C>(new LinkedHashSet<C>(cs));
    }

    public static FieldQuery<Field<?>> select(Field<?>... fields) {
        return select(Arrays.asList(fields));
    }

    public static FieldQuery<Field<?>> select(Collection<Field<?>> fields) {
        return new FieldQuery<Field<?>>(fields);
    }

    public static <C extends Component> Query<C> none() {
        return new Query<C>();
    }
}
