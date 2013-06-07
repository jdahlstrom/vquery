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
import static org.junit.Assert.fail;
import static org.vaadin.johannesd.vquery.VQuery.$;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public class TraversingTest {

    private class TestUI extends UI {
        TestUI() {
            setContent(new CssLayout(//
                    new CssLayout(//
                            new Button(),//
                            new Panel(//
                                    new Image()//
                            )//
                    ),//
                    new CssLayout(//
                    )//
            ));
        }

        @Override
        protected void init(VaadinRequest request) {
        }
    };

    UI ui;
    Query<Component> $ui;

    @Before
    public void setUp() {
        ui = new TestUI();
        $ui = $((Component) ui);
    }

    @After
    public void tearDown() {
        ui = null;
    }

    @Test
    public void testChildren() {
        CssLayout content = (CssLayout) ui.getContent();

        try {
            assertSame(content, $ui.children().one());
        } catch (RuntimeException e) {
            fail("Expected one child, " + $ui.children().size() + " found");
        }

        Query<Component> layoutChildren = $ui.children().children();
        assertEquals(2, layoutChildren.size());
        assertSame(content.getComponent(0), layoutChildren.index(0));
        assertSame(content.getComponent(1), layoutChildren.index(1));

        assertFalse(layoutChildren.children().children().children().exists());
    }

    @Test
    public void testDescendants() {
        Query<Component> descendants = $ui.descendants();

        assertEquals(6, descendants.size());
        assertSame(
                ((Panel) ((CssLayout) ((CssLayout) ui.getContent())
                        .getComponent(0)).getComponent(1)).getContent(),
                descendants.is(Image.class).one());

        Query<Component> descendants2 = $ui.children().children().descendants();
        assertEquals(3, descendants2.size());
    }

    @Test
    public void testParent() {
        try {
            assertSame(ui, $ui.children().parent().one());
        } catch (RuntimeException e) {
            fail("All children should have ui as their parent");
        }

        // Should contain all non-leaf nodes
        assertEquals(4, $ui.descendants().parent().size());
    }

    @Test
    public void testAncestors() {
        Image img = null;
        try {
            img = $ui.children().children().children().children()
                    .is(Image.class).one();
        } catch (RuntimeException e) {
            fail("There should be a single Image at depth 4");
        }

        Query<HasComponents> ancestors = $(img).ancestors();

        assertEquals(4, ancestors.size());

        Component p = img;
        for (Component c : ancestors) {
            p = p.getParent();
            assertSame(p, c);
        }
    }
}
