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

import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class VQueryTest extends AbstractQueryTest {

    private UI ui;
    private VerticalLayout uiContent;

    private Component[] all;
    private Field<?>[] fields;

    @Before
    public void setUp() {
        ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }
        };

        uiContent = new VerticalLayout();

        all = new Component[] { ui, uiContent, new Button(), new Label() };

        fields = new Field<?>[] { new TextField(), new CheckBox(),
                new ProgressIndicator() };

        uiContent.addComponents(all[2], all[3]);
        ui.setContent(uiContent);
    }

    @Test
    public void testAll() {
        UI.setCurrent(ui);
        try {
            assertEquals(all, VQuery.all());
        } finally {
            UI.setCurrent(null);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testAllWithoutCurrentUI() {
        VQuery.all();
    }

    @Test
    public void testSelect() {
        Query<?> selected = VQuery.select(ui, uiContent);
        Assert.assertEquals(new Query<Component>(ui, uiContent), selected);
    }

    @Test
    public void testSelectFields() {
        FieldQuery<?> selected = VQuery.select(fields);
        Assert.assertEquals(new FieldQuery<Field<?>>(Arrays.asList(fields)),
                selected);

    }

    @Test
    public void test$() {
        UI.setCurrent(ui);
        try {
            assertEquals(all, VQuery.$());
        } finally {
            UI.setCurrent(null);
        }

        Query<?> selected = VQuery.select(ui, uiContent);
        Assert.assertEquals(new Query<Component>(ui, uiContent), selected);
    }

    @Test(expected = IllegalStateException.class)
    public void test$WithoutCurrentUI() {
        VQuery.$();
    }

    @Test
    public void test$Fields() {
        Assert.assertEquals(new FieldQuery<Field<?>>(fields),
                VQuery.select(fields));
    }

    @Test
    public void testNone() {
        Query<Component> none = VQuery.none();
        assertFalse(none.exists());
    }
}
