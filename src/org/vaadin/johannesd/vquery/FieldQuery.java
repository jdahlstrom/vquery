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

import java.util.Collection;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Field;

public class FieldQuery<F extends Field<?>> extends
        AbstractQuery<F, FieldQuery<F>> {

    public <G extends F> FieldQuery(G field) {
        super(field);
    }

    public FieldQuery(AbstractQuery<? extends F, ?> query) {
        super(query.get());
    }

    public FieldQuery(Collection<? extends F> fs) {
        super(fs);
    }

    /*
     * Filtering
     */

    public FieldQuery<F> isValid(final boolean valid) {
        return filter(new Predicate<F>() {
            @Override
            public boolean apply(F f) {
                return f.isValid() == valid;
            }
        });
    }

    public FieldQuery<F> isRequired(final boolean required) {
        return filter(new Predicate<F>() {
            @Override
            public boolean apply(F f) {
                return f.isRequired() == required;
            }
        });
    }

    public FieldQuery<F> hasValue(final Object value) {
        return filter(new Predicate<F>() {
            @Override
            public boolean apply(F f) {
                return f.getValue() == null ? value == null : f.getValue()
                        .equals(value);
            }
        });
    }

    public <W> FieldQuery<Field<W>> hasValueType(final Class<W> valueType) {
        Set<Field<W>> result = set();
        for (Field<?> f : this) {
            if (valueType.isAssignableFrom(f.getType())) {
                result.add((Field<W>) f);
            }
        }
        return new FieldQuery<Field<W>>(result);
    }

    /*
     * Component manipulation
     */

    public FieldQuery<F> addValueChangeListener(ValueChangeListener listener) {
        for (Field<?> f : get()) {
            f.addValueChangeListener(listener);
        }
        return this;
    }

    public FieldQuery<F> setValue(Object value) {
        for (Field<?> f : get()) {
            f.setValue(value);
        }
    }

    public FieldQuery<F> setRequired(boolean required) {
        for (Field<?> f : get()) {
            f.setRequired(required);
        }
        return this;
    }

    public FieldQuery<F> setRequiredError(String error) {
        for (Field<?> f : get()) {
            f.setRequiredError(error);
        }
        return this;
    }

    @Override
    protected <G extends F> FieldQuery<F> createQuery(Set<G> gs) {
        return new FieldQuery<F>(gs);
    }
}
