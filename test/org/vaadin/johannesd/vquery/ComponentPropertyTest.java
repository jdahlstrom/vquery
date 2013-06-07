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
import static org.vaadin.johannesd.vquery.VQuery.$;

import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class ComponentPropertyTest {

    @Test
    public void testStyleNames() {
        Query<?> $ = $(new Button(), new Panel(), new Label()).addStyleName(
                "my-style my-other-style");

        assertEquals($, $.hasStyleName("my-style"));
        assertEquals($, $.hasStyleName("my-other-style"));

        $.index(0).removeStyleName("my-style");

        assertEquals($.slice(1, $.size()), $.hasStyleName("my-style"));
        assertEquals($, $.hasStyleName("my-other-style"));

        $.setStyleName("my-style");
        assertFalse($.hasStyleName("my-other-style").exists());

        $.setPrimaryStyleName("my-primary-name");
        assertEquals($, $.hasPrimaryStyleName("my-primary-name"));
    }
}
