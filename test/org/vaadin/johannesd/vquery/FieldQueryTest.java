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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class FieldQueryTest {

    private TextField textField;
    private CheckBox checkBox;
    private TextArea textArea;
    private Slider slider;
    private Table table;

    private Query<Component> components;
    private FieldQuery<Field<?>> fields;

    @Before
    public void setUp() {
        components = new Query<Component>(new Button(),
                textField = new TextField(), checkBox = new CheckBox(),
                new Panel(), textArea = new TextArea(), slider = new Slider(),
                new CssLayout(), table = new Table());
        fields = components.isField();
    }

    @Test
    public void testIsField() {
        assertEquals(5, fields.size());
        assertSame(textField, fields.index(0));
        assertSame(checkBox, fields.index(1));
        assertSame(textArea, fields.index(2));
        assertSame(slider, fields.index(3));
        assertSame(table, fields.index(4));

        assertSame(slider, components.isField(Slider.class).one());
        assertFalse(components.isField(OptionGroup.class).exists());

        assertSame(slider, fields.isField(Slider.class).one());
        assertFalse(fields.isField(OptionGroup.class).exists());
    }

    @Test
    public void testValueChangeListener() {
        class TestListener implements ValueChangeListener {
            int calls = 0;

            @Override
            public void valueChange(ValueChangeEvent event) {
                calls++;
            }
        }
        TestListener listener = new TestListener();

        fields.addValueChangeListener(listener);
        checkBox.setValue(true);
        textArea.setValue("TEST");

        assertEquals(2, listener.calls);

        fields.removeValueChangeListener(listener);

        textField.setValue("TEST");

        assertEquals(2, listener.calls);
    }

    @Test
    public void testHasValue() {
        fields.is(CheckBox.class).one().setValue(true);

        // The check box is the single field with value true
        assertEquals(checkBox, fields.hasValue(true).one());
        // No fields with value false
        assertFalse(fields.hasValue(false).exists());
        // No fields with value "foo"
        assertFalse(fields.hasValue("foo").exists());

        // The text field and the textarea have empty string as value
        FieldQuery<?> emptyStringFields = fields.hasValue("");
        assertEquals(2, emptyStringFields.size());
        assertSame(textField, emptyStringFields.index(0));
        assertSame(textArea, emptyStringFields.index(1));
    }

    @Test
    public void testHasValueType() {
        // Every value is-a Object
        assertEquals(fields, fields.hasValueType(Object.class));

        // No fields with value type Short
        assertFalse(fields.hasValueType(Short.class).exists());

        // The check box is the single Boolean field
        assertSame(checkBox, fields.hasValueType(Boolean.class).one());

        // The text field and the text area are String fields
        FieldQuery<Field<String>> stringFields = fields
                .hasValueType(String.class);
        assertEquals(2, stringFields.size());
        assertSame(textField, stringFields.index(0));
        assertSame(textArea, stringFields.index(1));
    }

    @Test
    public void testRequired() {
        fields.slice(0, 3).setRequired(true);
        assertEquals(fields.slice(0, 3), fields.isRequired(true));
        assertFalse(fields.slice(3, 5).isRequired(true).exists());

        fields.setRequired(false);
        assertFalse(fields.isRequired(true).exists());

        fields.index(2).setRequiredError("REQUIRED");
        assertEquals("REQUIRED", fields.index(2).getRequiredError());
    }

    @Test
    public void testValidation() {
        assertFalse(fields.isValid(false).exists());

        Validator v = new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (!"TEST".equals(value)) {
                    throw new InvalidValueException("");
                }
            }
        };

        fields.addValidator(v);

        assertFalse(fields.isValid(true).exists());

        textField.setValue("TEST");
        assertSame(textField, fields.isValid(true).one());

        fields.removeValidator(v);

        assertFalse(fields.isValid(false).exists());
    }
}
