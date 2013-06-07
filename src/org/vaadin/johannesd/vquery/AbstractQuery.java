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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.vaadin.johannesd.vquery.Filter.ByPredicate;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.SingleComponentContainer;

/**
 * Represents a set of Vaadin {@link Component components}
 * 
 * @author Johannes Dahlström
 * 
 * @param <C>
 *            The type of {@code Component} the query represents.
 * @param <This>
 *            The type of the class extending {@code AbstractQuery}.
 */
public abstract class AbstractQuery<C extends Component, This extends AbstractQuery<C, This>>
        implements Serializable, Iterable<C> {

    private Set<C> cs;

    /**
     * Constructs a query that represents the given components.
     */
    public AbstractQuery(C... cs) {
        this(Arrays.asList(cs));
    }

    /**
     * Constructs a query that represents the components in {@code cs}.
     */
    public AbstractQuery(Collection<? extends C> cs) {
        this.cs = Collections.unmodifiableSet(set(cs));
    }

    /**
     * Constructs a query that represents the same components as {@code q}.
     */
    public AbstractQuery(AbstractQuery<? extends C, ?> q) {
        this(q.get());
    }

    /**
     * Returns a query that is the union of {@code this} and {@code that}.
     */
    public <That extends AbstractQuery<? extends C, That>> This with(That that) {
        Set<C> result = set(cs);
        result.addAll(that.get());
        return createQuery(result);
    }

    /*************
     * Filtering *
     *************/

    /**
     * Returns a query with those components for which
     * {@link Predicate#apply(Component) p.apply()} returns true.
     */
    public This filter(Predicate<C> p) {
        return filter(new ByPredicate<C>(p));
    }

    /**
     * Returns a query with those components that have the given id. There
     * should be only one, but this is not enforced.
     * 
     * @param id
     *            The id. Cannot be null.
     */
    public This id(final String id) {
        return filter(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                return id.equals(c.getId());
            }
        });
    }

    /**
     * Returns a query with those components in this query that have the given
     * stylename.
     */
    public This hasStyleName(final String styleName) {
        return filter(new ByPredicate<C>(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                return Arrays.asList(c.getStyleName().split("\\s+")).contains(
                        styleName);
            }
        }));
    }

    /**
     * Returns a query with those components in this query that have the given
     * primary stylename.
     */
    public This hasPrimaryStyleName(final String styleName) {
        return filter(new ByPredicate<C>(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                return c.getPrimaryStyleName().equals(styleName);
            }
        }));
    }

    /**
     * Returns a query with those components in this query for which
     * {@code isVisible() == visible}.
     */
    public This isVisible(final boolean visible) {
        return filter(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                return c.isVisible() == visible;
            }
        });
    }

    /**
     * Returns a query with those components in this query for which
     * {@code isEnabled() == enabled}.
     */
    public This isEnabled(final boolean enabled) {
        return filter(new Predicate<C>() {
            @Override
            public boolean apply(Component c) {
                return c.isEnabled() == enabled;
            }
        });
    }

    /**
     * Returns a query with those components in this query for which
     * {@code isReadOnly() == readOnly}.
     */
    public This isReadOnly(final boolean readOnly) {
        return filter(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                return c.isReadOnly() == readOnly;
            }
        });
    }

    /**
     * Returns a query with those components in this query that either have or
     * do not have at least one component child, depending on whether
     * {@code leaf} is true or false, respectively.
     * <p>
     * Note that layouts and other component containers are leaf nodes iff they
     * are empty.
     */
    public This isLeaf(final boolean leaf) {
        return filter(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                return (c instanceof HasComponents && ((HasComponents) c)
                        .iterator().hasNext()) != leaf;
            }
        });
    }

    /**
     * Returns a query with those components that are
     */
    public This isAttached(final boolean attached) {
        return filter(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                // TODO use c.isAttached() once available
                return (c.getUI() != null && c.getUI().getSession() != null) == attached;
            }
        });
    }

    /**
     * If this query includes c, returns a query with only c. Otherwise, returns
     * an empty query.
     */
    public <D extends Component> Query<D> is(D c) {
        if (cs.contains(c)) {
            return new Query<D>(c);
        } else {
            return new Query<D>();
        }
    }

    /**
     * Returns a query with those components whose type is the given class.
     */
    public <D extends C> Query<D> is(Class<D> klass) {
        Set<D> result = set();
        for (C c : cs) {
            if (klass.isAssignableFrom(c.getClass())) {
                result.add(klass.cast((c)));
            }
        }
        return new Query<D>(result);
    }

    public This isAlso(Class<?> klass) {
        Set<C> result = set();
        for (C c : cs) {
            if (klass.isAssignableFrom(c.getClass())) {
                result.add(c);
            }
        }
        return createQuery(result);
    }

    /**
     * Returns a FieldQuery with all the components in this query that are
     * fields.
     */
    public FieldQuery<Field<?>> isField() {
        Set<Field<?>> result = set();
        for (C c : cs) {
            if (c instanceof Field<?>) {
                result.add((Field<?>) c);
            }
        }
        return new FieldQuery<Field<?>>(result);
    }

    /**
     * Returns a FieldQuery with all the components in this query that are
     * fields of the given type.
     */
    public <F extends Field<?>> FieldQuery<F> isField(Class<F> fieldType) {
        Set<F> result = set();
        for (C c : cs) {
            if (c instanceof Field<?>
                    && fieldType.isAssignableFrom(c.getClass())) {
                @SuppressWarnings("unchecked")
                F f = (F) c;
                result.add(f);
            }
        }
        return new FieldQuery<F>(result);
    }

    /**************************
     * Component manipulation *
     **************************/

    /**
     * Adds the given stylename to all the components in this query.
     */
    public This addStyleName(String sn) {
        for (Component c : cs) {
            c.addStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Removes the given stylename from all the components in this query.
     */
    public This removeStyleName(String sn) {
        for (Component c : cs) {
            c.removeStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Sets the stylename of all the components in this query.
     */
    public This setStyleName(String sn) {
        for (Component c : cs) {
            c.setStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Sets the primary stylename of all the components in this query.
     */
    public This setPrimaryStyleName(String sn) {
        for (Component c : cs) {
            c.setPrimaryStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Sets the visibility of all the components in this query to
     * {@code visible.}
     */
    public This setVisible(boolean visible) {
        for (C c : cs) {
            c.setVisible(visible);
        }
        return createQuery();
    }

    /**
     * Sets the enabled state of all the components in this query to
     * {@code enabled.}
     */
    public This setEnabled(boolean enabled) {
        for (C c : cs) {
            c.setEnabled(enabled);
        }
        return createQuery();
    }

    /**
     * Sets the readonly state of all the components in this query to
     * {@code readOnly.}
     */
    public This setReadOnly(boolean readOnly) {
        for (C c : cs) {
            c.setReadOnly(readOnly);
        }
        return createQuery();
    }

    /**************************
     * Hierarchy manipulation *
     **************************/

    /**
     * Adds all the components in this query to {@code cc}, removing them from
     * their original parents if any.
     */
    public This addTo(ComponentContainer cc) {
        cc.addComponents(cs.toArray(new Component[size()]));
        return (This) this;
    }

    /**
     * Adds all the components in this query to the given index in {@code aol},
     * removing them from their original parents if any.
     */
    public This addTo(AbstractOrderedLayout aol, int index) {
        for (C c : this) {
            aol.addComponent(c, index++);
        }
        return createQuery();
    }

    /**
     * Adds all the components in this query to the given index in {@code cssl},
     * removing them from their original parents if any.
     */
    public This addTo(CssLayout cssl, int index) {
        Component previous = null;
        for (C c : this) {
            int i = previous == null ? index
                    : cssl.getComponentIndex(previous) + 1;
            cssl.addComponent(c, i);
            previous = c;
        }
        return createQuery();
    }

    /**
     * Removes all the components in this query from their parent(s).
     */
    public This remove() {
        for (C c : this) {
            HasComponents parent = c.getParent();
            if (parent instanceof ComponentContainer) {
                ((ComponentContainer) parent).removeComponent(c);
            } else if (parent instanceof SingleComponentContainer) {
                ((SingleComponentContainer) parent).setContent(null);
            }
        }
        return createQuery();
    }

    /**
     * Removes from {@code parent} all the components in this query that are
     * children of {@code parent}.
     */
    public This removeFrom(HasComponents parent) {
        for (C c : this) {
            if (c.getParent() == parent) {
                if (parent instanceof ComponentContainer) {
                    ((ComponentContainer) parent).removeComponent(c);
                } else if (parent instanceof SingleComponentContainer) {
                    ((SingleComponentContainer) parent).setContent(null);
                }
            }
        }
        return createQuery();
    }

    /*****************
     * Set accessors *
     *****************/

    /**
     * Returns an iterator yielding all the components in this query.
     */
    @Override
    public Iterator<C> iterator() {
        return cs.iterator();
    }

    /**
     * Returns a set containing all the components in this query.
     */
    public Set<? extends C> get() {
        return set(cs);
    }

    /**
     * Returns the component at the given index.
     * 
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds.
     */
    public C index(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException();
        }
        Iterator<C> i = cs.iterator();
        while (index > 0) {
            index--;
            i.next();
        }
        return i.next();
    }

    /**
     * Returns a query containing those components whose index is in the range
     * [from, to).
     * 
     * @throws IndexOutOfBoundsException
     *             if the indices are out of rance of if to < from.
     */
    public This slice(int from, int to) {
        if (from < 0 || from >= size() || to < from || from > size()) {
            throw new IndexOutOfBoundsException();
        }
        Set<C> result = set();
        Iterator<C> i = cs.iterator();
        to -= from;
        while (from > 0) {
            from--;
            i.next();
        }
        while (to > 0) {
            to--;
            result.add(i.next());
        }

        return createQuery(result);
    }

    /**
     * If this query contains exactly one component, returns the component.
     * Otherwise, throws.
     */
    public C one() {
        if (cs.size() != 1) {
            throw new RuntimeException(
                    "Set does not contain exactly one element");
        }
        return cs.iterator().next();
    }

    /**
     * Returns whether this query contains at least one component.
     * 
     * @return
     */
    public boolean exists() {
        return !cs.isEmpty();
    }

    /**
     * Returns the number of components in this query.
     */
    public int size() {
        return cs.size();
    }

    /*
     * Helpers
     */

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass()
                && cs.equals(((AbstractQuery<?, ?>) o).cs);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + cs;
    }

    abstract protected <D extends C> This createQuery(Set<D> cs);

    protected This createQuery() {
        return createQuery(get());
    }

    protected <D extends Component> AbstractQuery<D, ?> map(Map<C, D> m) {
        return new Query<D>(m.apply(cs));
    }

    protected This filter(Filter<C> f) {
        return createQuery(f.apply(cs));
    }

    protected static <D extends Component> Set<D> set() {
        return set(Collections.<D> emptySet());
    }

    protected static <D extends Component> Set<D> set(Collection<? extends D> cs) {
        return new LinkedHashSet<D>(cs);
    }
}
