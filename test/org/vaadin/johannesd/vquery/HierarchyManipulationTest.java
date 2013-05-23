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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.vaadin.johannesd.vquery.VQuery.$;

import org.junit.Test;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class HierarchyManipulationTest {

    private class Button extends com.vaadin.ui.Button {

        Button() {
        }

        Button(String caption) {
            super(caption);
        }

        @Override
        public String toString() {
            return "Button " + getCaption();
        }
    }

    @Test
    public void testAddTo() {
        CssLayout layout1 = new CssLayout();
        CssLayout layout2 = new CssLayout();
        CssLayout layout3 = new CssLayout();
        Button b1 = new Button();
        Button b2 = new Button();

        $(b1).addTo(layout1);
        assertSame(layout1.getComponent(0), b1);

        $(b2).addTo(layout1);
        assertSame(layout1.getComponent(0), b1);
        assertSame(layout1.getComponent(1), b2);

        $(b1).addTo(layout1);
        assertSame(layout1.getComponent(0), b2);
        assertSame(layout1.getComponent(1), b1);

        $(b2).addTo(layout2);
        assertSame(layout1.getComponent(0), b1);
        assertSame(layout2.getComponent(0), b2);

        $(b1, b2).addTo(layout3);
        assertSame(layout3.getComponent(0), b1);
        assertSame(layout3.getComponent(1), b2);
    }

    @Test
    public void testAddToIndex() {
        CssLayout layout1 = new CssLayout(new Button("c1"), new Button("c2"));
        AbstractOrderedLayout layout2 = new VerticalLayout(new Button("o1"),
                new Button("o2"));

        Button b1 = new Button("B1");
        Button b2 = new Button("B2");

        $(b1, b2).addTo(layout1, 0);
        assertSame(layout1.getComponent(0), b1);
        assertSame(layout1.getComponent(1), b2);

        $(b1, b2).addTo(layout1, 4);
        assertSame(layout1.getComponent(2), b1);
        assertSame(layout1.getComponent(3), b2);

        $(b1, b2).addTo(layout1, 1);
        assertSame(layout1.getComponent(1), b1);
        assertSame(layout1.getComponent(2), b2);

        $(b1, b2).addTo(layout2, 1);
        assertSame(layout2.getComponent(1), b1);
        assertSame(layout2.getComponent(2), b2);
    }

    @Test
    public void testRemove() {
        Button b1a = new Button("b1a");
        Button b1b = new Button("b1b");
        Button b2a = new Button("b2a");
        Button b2b = new Button("b2b");
        CssLayout layout1 = new CssLayout(b1a, b1b);
        CssLayout layout2 = new CssLayout(b2a, b2b);

        $(b1a, b2b).remove();
        assertNull(b1a.getParent());
        assertNull(b2b.getParent());

        try {
            $(b1a).remove();
        } catch (Exception e) {
            fail("Removing a detached component should be a no-op");
        }

        $(b1b, b2a).removeFrom(layout1);
        assertNull(b1b.getParent());
        assertSame(layout2, b2a.getParent());

        Panel panel = new Panel(b1a);
        $(b1a, b1b).removeFrom(panel);
        assertNull(b1b.getParent());
        assertSame(layout2, b2a.getParent());
    }
}
