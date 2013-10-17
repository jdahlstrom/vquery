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

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.SingleComponentContainer;

/**
 * Represents a set of Vaadin {@link Component components}.
 * <p>
 * When extending this class, the type of the extending class itself should be
 * given as the {@code This} type parameter. The parameter is used as the return
 * type of all the methods returning a set with the component type equal to
 * {@code C}, enabling proper method chaining. The abstract factory method
 * {@code createQuery} should be overridden like in the following example:
 * <p>
 * 
 * <pre>
 * // A specialized query for Fields
 * public class FieldQuery{@code <F extends Field>}
 *         extends AbstractQuery{@code <F, MyQuery<F extends Field>>} {
 * 
 *     {@code @}Override
 *     protected {@code <G extends F> FieldQuery<F> createQuery(Set<G> gs)} {
 *         return new FieldQuery<F>(gs);
 *     }
 * }
 * </pre>
 * 
 * @see Query
 * @see FieldQuery
 * 
 * @author Johannes Dahlström
 * 
 * @param <C>
 *            The type of the {@code Component}s the query represents.
 * @param <This>
 *            The type of the class extending {@code AbstractQuery}. Used in
 *            return values to enable method chaining.
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
     * Returns the union of {@code this} and {@code that}.
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
     * Returns the subset of components in this set for which
     * {@link Predicate#apply(Component) p.apply()} returns true.
     * 
     * @param p
     *            The predicate used for filtering. Cannot be null.
     */
    public This filter(Predicate<? super C> p) {
        return filter(new ByPredicate<C>(p));
    }

    /**
     * Returns the subset of components in this set that have the given id.
     * There should be only one, but this is not enforced. The {@link #one()}
     * method may be used to ensure there is exactly one such component.
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
     * Returns the subset of components in this set that have the given
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
     * Returns the subset of components in this set that have the given primary
     * stylename.
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
     * Returns the subset of components in this set for which
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
     * Returns the subset of components in this set for which
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
     * Returns the subset of components in this set for which
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
     * If the parameter {@code leaf} is true, returns the subset of components
     * in this set that are leaf nodes, that is, those that do not have any
     * children. Otherwise, returns those that do have children.
     * <p>
     * Note that layouts and other component containers that do not have any
     * children are considered leaf nodes.
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
     * Returns the subset of components in this set for which
     * {@code isAttached() == attached}.
     */
    public This isAttached(final boolean attached) {
        return filter(new Predicate<C>() {
            @Override
            public boolean apply(C c) {
                return c.isAttached() == attached;
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
     * Returns the subset of components in this set that are instances of the
     * given subtype of {@link C}.
     * <p>
     * Example:
     * <p>
     * {@code myQuery.is(Button.class)} returns a {@code Query<Button>}.
     * 
     * @return a Query with the given component type.
     */
    public <D extends C> Query<D> is(Class<D> klass) {
        Set<D> result = set();
        for (C c : this) {
            if (klass.isAssignableFrom(c.getClass())) {
                result.add(klass.cast((c)));
            }
        }
        return new Query<D>(result);
    }

    /**
     * Returns the subset of components in this set that are instances of the
     * given interface in addition to {@code C}.
     */
    public This is(Class<?> klass) {
        Set<C> result = set();
        for (C c : this) {
            if (klass.isAssignableFrom(c.getClass())) {
                result.add(c);
            }
        }
        return createQuery(result);
    }

    /**
     * Returns a FieldQuery instance representing the subset of components in
     * this set that are fields.
     */
    public FieldQuery<Field<?>> isField() {
        Set<Field<?>> result = set();
        for (C c : this) {
            if (c instanceof Field<?>) {
                result.add((Field<?>) c);
            }
        }
        return new FieldQuery<Field<?>>(result);
    }

    /**
     * Returns a FieldQuery representing the subset of components in this set
     * that are fields of the given type.
     */
    public <F extends Field<?>> FieldQuery<F> isField(Class<F> fieldType) {
        Set<F> result = set();
        for (C c : this) {
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
     * Adds the given stylename to all the components in this set.
     */
    public This addStyleName(String sn) {
        for (C c : this) {
            c.addStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Removes the given stylename from all the components in this set.
     */
    public This removeStyleName(String sn) {
        for (C c : this) {
            c.removeStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Sets the stylename of all the components in this query.
     */
    public This setStyleName(String sn) {
        for (C c : this) {
            c.setStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Sets the primary stylename of all the components in this query.
     */
    public This setPrimaryStyleName(String sn) {
        for (C c : this) {
            c.setPrimaryStyleName(sn);
        }
        return createQuery();
    }

    /**
     * Sets the visibility of all the components in this query to
     * {@code visible.}
     */
    public This setVisible(boolean visible) {
        for (C c : this) {
            c.setVisible(visible);
        }
        return createQuery();
    }

    /**
     * Sets the enabled state of all the components in this query to
     * {@code enabled.}
     */
    public This setEnabled(boolean enabled) {
        for (C c : this) {
            c.setEnabled(enabled);
        }
        return createQuery();
    }

    /**
     * Sets the readonly state of all the components in this query to
     * {@code readOnly.}
     */
    public This setReadOnly(boolean readOnly) {
        for (C c : this) {
            c.setReadOnly(readOnly);
        }
        return createQuery();
    }

    /**
     * Sets the size of all the components in this set to 100% x 100%.
     * 
     * @see Component#setSizeFull()
     */
    public This setSizeFull() {
        for (C c : this) {
            c.setSizeFull();
        }
        return createQuery();
    }

    /**
     * Sets the size of all the components in this set to undefined.
     * 
     * @see Component#setSizeUndefined()
     */
    public This setSizeUndefined() {
        for (C c : this) {
            c.setSizeUndefined();
        }
        return createQuery();
    }

    /**
     * Sets the width of all the components in this set.
     * 
     * @see Component#setWidth(String).
     */
    public This setWidth(String width) {
        for (C c : this) {
            c.setWidth(width);
        }
        return createQuery();
    }

    /**
     * Sets the width of all the components in this set.
     * 
     * @see Component#setWidth(float, Unit).
     */
    public This setWidth(float width, Unit unit) {
        for (C c : this) {
            c.setWidth(width, unit);
        }
        return createQuery();
    }

    /**
     * Sets the height of all the components in this set.
     * 
     * @see Component#setHeight(String)
     */
    public This setHeight(String height) {
        for (C c : this) {
            c.setHeight(height);
        }
        return createQuery();
    }

    /**
     * Sets the height of all the components in this set.
     * 
     * @see Component#setHeight(float, Unit)
     */
    public This setHeight(float height, Unit unit) {
        for (C c : cs) {
            c.setHeight(height, unit);
        }
        return createQuery();
    }

    /***********************
     * Hierarchy traversal *
     ***********************/

    /**
     * Returns the set of all the descendants of all the components in this set.
     * <p>
     * Component c is a descendant of component d if and only if the parent of c
     * is d or a descendant of d.
     */
    public Query<Component> descendants() {
        return new Query<Component>(Map.descendants.apply(get()));
    }

    public Query<Component> descendants(int depth) {
        return null; // TODO stub
    }

    /**
     * Returns the set of all the children of all the components in this set.
     */
    public Query<Component> children() {
        return new Query<Component>(Map.children.apply(get()));
    }

    /**
     * Returns the set of the parents of the components in this set.
     */
    public Query<HasComponents> parent() {
        return new Query<HasComponents>(Map.parent.apply(get()));
    }

    /**
     * Returns the set of all the ancestors of all the components in this set.
     * <p>
     * Component c is an ancestor of component d if c is the parent of d or an
     * ancestor of the parent.
     */
    public Query<HasComponents> ancestors() {
        return new Query<HasComponents>(Map.ancestors.apply(get()));
    }

    public Query<HasComponents> ancestor(int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative");
        } else if (depth == 0) {
            return parent();
        } else {
            return parent().ancestor(depth - 1);
        }
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
        return createQuery();
    }

    /**
     * Adds all the components in this query to the given index in
     * {@code layout}, removing them from their original parents if any.
     */
    public This addTo(AbstractOrderedLayout layout, int index) {
        return addTo((ComponentContainer) layout, index);
    }

    /**
     * Adds all the components in this query to the given index in
     * {@code layout}, removing them from their original parents if any.
     */
    public This addTo(CssLayout layout, int index) {
        return addTo((ComponentContainer) layout, index);
    }

    /**
     * Adds the components in this set after c if the parent of c supports it.
     * 
     * @throws IllegalArgumentException
     *             if c has no parent or the parent does not support adding
     *             components at a specified index.
     */
    public This addAfter(Component c) {
        return addTo(c.getParent(), getComponentIndex(c) + 1);
    }

    /**
     * Adds the components in this set before c if the parent of c supports it.
     * 
     * @throws IllegalArgumentException
     *             if c has no parent or the parent does not support adding
     *             components at a specified index.
     */
    public This addBefore(Component c) {
        return addTo(c.getParent(), getComponentIndex(c));
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
     * Removes from {@code parent} all the components in this query that are its
     * children.
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
     * Returns an iterator yielding all the components in this set in original
     * insertion order.
     */
    @Override
    public Iterator<C> iterator() {
        return cs.iterator();
    }

    /**
     * Returns a {@link Set} containing all the components in this set.
     */
    public Set<C> get() {
        return set(cs);
    }

    /**
     * Returns the component at the given index in this set. The indices are
     * based on the original insertion order.
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
     * Returns a query containing those components whose index is in the range [
     * {@code from}, {@code to}). The indices are based on the original
     * insertion order.
     * 
     * @throws IndexOutOfBoundsException
     *             if {@code from} or {@code to} is out of bounds or if
     *             {@code to} < {@codefrom}.
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
     * Returns the first component in this set.
     * 
     * @throws IndexOutOfBoundsException
     *             if the set is empty.
     */
    public C first() {
        return index(0);
    }

    /**
     * Returns the last component in this set.
     * 
     * @throws IndexOutOfBoundsException
     *             if the set is empty.
     */
    public C last() {
        return index(size() - 1);
    }

    /**
     * If this set contains exactly one component, returns the component.
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
     * Returns whether this set contains at least one component.
     * 
     * @return
     */
    public boolean exists() {
        return !cs.isEmpty();
    }

    /**
     * Returns the number of components in this set.
     */
    public int size() {
        return cs.size();
    }

    /*
     * Helpers
     */

    /**
     * Returns whether this set is equal to {@code that}. An
     * {@code AbstractQuery} instance is equal to another object if and only if
     * the are of the same type and contain the same components, disregarding
     * order.
     */
    @Override
    public boolean equals(Object that) {
        return getClass() == that.getClass()
                && cs.equals(((AbstractQuery<?, ?>) that).cs);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + cs;
    }

    /**
     * Returns a new {@code This} instance containing the same components as
     * {@code cs}.
     */
    abstract protected <D extends C> This createQuery(Set<D> cs);

    protected This createQuery() {
        return createQuery(get());
    }

    /**
     * Returns the set of components returned by the given {@link Map} when
     * applied to this set.
     */
    protected <D extends Component> AbstractQuery<D, ?> map(Map<C, D> m) {
        return new Query<D>(m.apply(cs));
    }

    /**
     * Returns the subset of components returned by the given {@link Filter}
     * when applied to this set.
     */
    protected This filter(Filter<C> f) {
        return createQuery(f.apply(cs));
    }

    /**
     * Returns a new empty set of the appropriate type.
     */
    protected static <D extends Component> Set<D> set() {
        return set(Collections.<D> emptySet());
    }

    /**
     * Returns a new set of the appropriate type, containing the same elements
     * as {@code cs}.
     */
    protected static <D extends Component> Set<D> set(Collection<? extends D> cs) {
        return new LinkedHashSet<D>(cs);
    }

    /**
     * Calls {@code parent.addComponent(c, index)}.
     * 
     * @throws IllegalArgumentException
     *             if {@code parent} is not a CssLayout or an
     *             AbstractOrderedLayout.
     */
    protected static void addComponent(HasComponents parent, Component c,
            int index) {
        if (parent instanceof CssLayout) {
            ((CssLayout) parent).addComponent(c, index);
        } else if (parent instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) parent).addComponent(c, index);
        } else {
            throw new IllegalArgumentException(
                    "Parent is not a CssLayout or an AbstractOrderedLayout");
        }
    }

    /**
     * Returns {@code c.getParent().getComponentIndex(c)}.
     * 
     * @throws IllegalArgumentException
     *             if {@code c.getParent()} is not a CssLayout or an
     *             AbstractOrderedLayout.
     */
    protected static int getComponentIndex(Component c) {
        Component parent = c.getParent();
        if (parent instanceof CssLayout) {
            return ((CssLayout) parent).getComponentIndex(c);
        } else if (parent instanceof AbstractOrderedLayout) {
            return ((AbstractOrderedLayout) parent).getComponentIndex(c);
        } else {
            throw new IllegalArgumentException(
                    "Parent is not a CssLayout or an AbstractOrderedLayout");
        }
    }

    /**
     * Adds the components in this set to {@code parent} at {@code index}.
     * 
     * @throws IllegalArgumentException
     *             if {@code c.getParent()} is not a CssLayout or an
     *             AbstractOrderedLayout.
     */
    protected This addTo(HasComponents parent, int index) {
        for (C c : this) {
            boolean wasAlreadyChild = c.getParent() == parent;
            addComponent(parent, c, index);
            index = (wasAlreadyChild ? getComponentIndex(c) : index) + 1;
        }
        return createQuery();
    }
}
