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
import static org.junit.Assert.assertTrue;
import static org.vaadin.johannesd.vquery.VQuery.$;
import static org.vaadin.johannesd.vquery.VQuery.none;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class ComponentPropertyTest {

    private Query<Component> some;
    private Query<Component> none;

    @Before
    public void setUp() {
        some = $((Component) new Button(), new Panel(), new Label());
        none = none();
    }

    @Test
    public void testStyleNames() {
        testStyleNames(some);
        testStyleNames(none);
    }

    protected void testStyleNames(Query<?> $) {
        assertFalse($.hasStyleName("my-style").exists());
        assertFalse($.hasPrimaryStyleName("my-primary-name").exists());

        $.addStyleName("my-style my-other-style");

        assertEquals($, $.hasStyleName("my-style"));
        assertEquals($, $.hasStyleName("my-other-style"));

        $.removeStyleName("my-style");

        assertFalse($.hasStyleName("my-style").exists());
        assertEquals($, $.hasStyleName("my-other-style"));

        $.setStyleName("my-style");
        assertFalse($.hasStyleName("my-other-style").exists());

        $.setStyleName("");
        assertFalse($.hasStyleName("my-style").exists());

        $.setPrimaryStyleName("my-primary-name");
        assertEquals($, $.hasPrimaryStyleName("my-primary-name"));
    }

    @Test
    public void testSizing() {
        testSizing(some);
        testSizing(none);
    }

    protected void testSizing(Query<Component> $) {
        $.setSizeFull();
        for (Component c : $) {
            assertEquals(100, c.getWidth(), 0);
            assertEquals(100, c.getHeight(), 0);
            assertEquals(Unit.PERCENTAGE, c.getWidthUnits());
            assertEquals(Unit.PERCENTAGE, c.getHeightUnits());
        }

        $.setSizeUndefined();
        for (Component c : $) {
            assertEquals(-1, c.getWidth(), 0);
            assertEquals(-1, c.getHeight(), 0);
            assertEquals(Unit.PIXELS, c.getWidthUnits());
            assertEquals(Unit.PIXELS, c.getHeightUnits());
        }

        $.setWidth("50px");
        for (Component c : $) {
            assertEquals(50, c.getWidth(), 0);
            assertEquals(Unit.PIXELS, c.getWidthUnits());
        }

        $.setHeight("50px");
        for (Component c : $) {
            assertEquals(50, c.getHeight(), 0);
            assertEquals(Unit.PIXELS, c.getHeightUnits());
        }
    }

    public void testBooleans() {
        testBooleans(some);
        testBooleans(none);
    }

    protected void testBooleans(Query<Component> $) {
        $.setEnabled(false).setReadOnly(true).setVisible(false);
        for (Component c : $) {
            assertFalse(c.isEnabled());
            assertTrue(c.isReadOnly());
            assertFalse(c.isVisible());
        }

        $.setEnabled(true);
        for (Component c : $) {
            assertTrue(c.isEnabled());
            assertTrue(c.isReadOnly());
            assertFalse(c.isVisible());
        }
    }
}
