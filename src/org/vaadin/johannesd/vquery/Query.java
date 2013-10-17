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

import com.vaadin.ui.Component;

public class Query<C extends Component> extends AbstractQuery<C, Query<C>> {

    public Query(C... cs) {
        super(cs);
    }

    public Query(Collection<? extends C> cs) {
        super(cs);
    }

    }

    @Override
    protected <D extends C> Query<C> createQuery(Set<D> cs) {
        return new Query<C>(cs);
    }
}
