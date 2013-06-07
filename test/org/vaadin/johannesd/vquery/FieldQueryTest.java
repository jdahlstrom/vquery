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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;

public class FieldQueryTest {

    private int listenerCalls = 0;

    @Test
    public void test() {
        Query<Component> q = new Query<Component>(new Button(), new CheckBox(),
                new Panel(), new TextArea());

        FieldQuery<Field<?>> fields = q.isField();

        assertSame(q.index(1), fields.index(0));
        assertSame(q.index(3), fields.index(1));

        assertSame(q.index(1), fields.hasValueType(Boolean.class).one());
        assertSame(q.index(3), fields.hasValue("").one());

        ValueChangeListener listener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                listenerCalls++;
            }
        };

        fields.addValueChangeListener(listener);

        fields.isField(CheckBox.class).one().setValue(true);
        fields.isField(TextArea.class).one().setValue("FOO");

        assertEquals(2, listenerCalls);
    }
}
