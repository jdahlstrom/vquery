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

import java.util.Iterator;

import org.junit.Assert;

import com.vaadin.ui.Component;

public abstract class AbstractQueryTest {

    protected void assertEquals(Component[] expected, AbstractQuery<?, ?> actual) {
        Assert.assertEquals(expected.length, actual.size());
        for (Component c : expected) {
            Assert.assertTrue(actual.is(c).exists());
        }
    }

    protected void assertEqualsOrdered(Component[] expected,
            AbstractQuery<?, ?> actual) {
        Assert.assertEquals(expected.length, actual.size());
        Iterator<?> ai = actual.iterator();
        for (Component c : expected) {
            Assert.assertSame(c, ai.next());
        }
    }
}
