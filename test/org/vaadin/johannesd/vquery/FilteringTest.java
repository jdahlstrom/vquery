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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.vaadin.johannesd.vquery.VQuery.$;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class FilteringTest {

    Component[] cs;
    Query<Component> $;

    @Before
    public void setUp() {
        cs = new Component[] { new Button(), new TextField(), new Panel(),
                new VerticalLayout(new Button()), new TextArea(),
                new CheckBox() };
        $ = $(cs);
    }

    @Test
    public void testIs() {
        assertArrayEquals(cs, $.is(Component.class).get().toArray());

        assertSame(cs[0], $.is(Button.class).one());

        assertSame(cs[2], $.is(cs[2]).one());

        Query<AbstractTextField> atf = $.is(AbstractTextField.class);

        assertEquals(2, atf.size());
        assertSame(cs[1], atf.index(0));
        assertSame(cs[4], atf.index(1));

        assertFalse($.is(Label.class).exists());
    }

    @Test
    public void testId() {
        cs[2].setId("my-id");
        try {
            assertSame(cs[2], $.id("my-id").one());
        } catch (Exception e) {
            fail("Should have exactly one component with my-id");
        }
    }

    @Test
    public void testHasStyleName() {
        assertFalse($.hasStyleName("my-style").exists());
        cs[3].addStyleName("my-style my-other-style");
        try {
            assertSame(cs[3], $.hasStyleName("my-style").one());
            assertSame(cs[3], $.hasStyleName("my-other-style").one());
        } catch (Exception e) {
            fail("Should have exactly one component with my-style");
        }
    }

    @Test
    public void testIsField() {
        FieldQuery<?> fields = $.isField();
        assertEquals(3, fields.size());
        assertSame(cs[1], fields.index(0));
        assertSame(cs[4], fields.index(1));
        assertSame(cs[5], fields.index(2));

        FieldQuery<?> checkbox = fields.isField(CheckBox.class);
        assertSame(cs[5], checkbox.one());
    }

    @Test
    public void testIsLeaf() {
        Query<Component> leaves = $.isLeaf(true);
        assertEquals(5, leaves.size());
        assertTrue(leaves.is(cs[0]).exists());
        assertTrue(leaves.is(cs[1]).exists());
        assertTrue(leaves.is(cs[2]).exists());
        assertTrue(leaves.is(cs[4]).exists());
        assertTrue(leaves.is(cs[5]).exists());
    }
}
