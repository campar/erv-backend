package com.example.erv.specification;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.criteria.CollectionJoin;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.MapJoin;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.SetJoin;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

public class JoinRoot<T> implements Root<T> {
    private final Join<?, T> join;

    public JoinRoot(Join<?, T> join) {
        this.join = join;
    }

    @Override
    public EntityType<T> getModel() {
        // this one is the only one that cannot be delegated, although it's not used in my use case
        throw new UnsupportedOperationException("getModel cannot be delegated to a JoinRoot");
    }

    @Override
    public Path<?> getParentPath() {
        return this.join.getParentPath();
    }

    @Override
    public <Y> Path<Y> get(SingularAttribute<? super T, Y> attribute) {
        return this.join.get(attribute);
    }

    @Override
    public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<T, C, E> collection) {
        return this.join.get(collection);
    }

    @Override
    public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<T, K, V> map) {
        return this.join.get(map);
    }

    @Override
    public Expression<Class<? extends T>> type() {
        return this.join.type();
    }

    @Override
    public <Y> Path<Y> get(String attributeName) {
        return this.join.get(attributeName);
    }

    @Override
    public Set<Join<T, ?>> getJoins() {
        return this.join.getJoins();
    }

    @Override
    public boolean isCorrelated() {
        return this.join.isCorrelated();
    }

    @Override
    @SuppressWarnings("unchecked")
    public From<T, T> getCorrelationParent() {
        return (From<T, T>) this.join.getCorrelationParent();
    }

    @Override
    public <Y> Join<T, Y> join(SingularAttribute<? super T, Y> attribute) {
        return this.join.join(attribute);
    }

    @Override
    public <Y> Join<T, Y> join(SingularAttribute<? super T, Y> attribute, JoinType jt) {
        return this.join.join(attribute, jt);
    }

    @Override
    public <Y> CollectionJoin<T, Y> join(CollectionAttribute<? super T, Y> collection) {
        return this.join.join(collection);
    }

    @Override
    public <Y> SetJoin<T, Y> join(SetAttribute<? super T, Y> set) {
        return this.join.join(set);
    }

    @Override
    public <Y> ListJoin<T, Y> join(ListAttribute<? super T, Y> list) {
        return null;
    }

    @Override
    public <K, V> MapJoin<T, K, V> join(MapAttribute<? super T, K, V> map) {
        return null;
    }

    @Override
    public <Y> CollectionJoin<T, Y> join(CollectionAttribute<? super T, Y> collection, JoinType jt) {
        return null;
    }

    @Override
    public <Y> SetJoin<T, Y> join(SetAttribute<? super T, Y> set, JoinType jt) {
        return null;
    }

    @Override
    public <Y> ListJoin<T, Y> join(ListAttribute<? super T, Y> list, JoinType jt) {
        return null;
    }

    @Override
    public <K, V> MapJoin<T, K, V> join(MapAttribute<? super T, K, V> map, JoinType jt) {
        return null;
    }

    @Override
    public <X, Y> Join<X, Y> join(String attributeName) {
        return null;
    }

    @Override
    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
        return null;
    }

    @Override
    public <X, Y> SetJoin<X, Y> joinSet(String attributeName) {
        return null;
    }

    @Override
    public <X, Y> ListJoin<X, Y> joinList(String attributeName) {
        return null;
    }

    @Override
    public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName) {
        return null;
    }

    @Override
    public <X, Y> Join<X, Y> join(String attributeName, JoinType jt) {
        return null;
    }

    @Override
    public <X, Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
        return null;
    }

    @Override
    public <X, Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
        return null;
    }

    @Override
    public <X, Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
        return null;
    }

    @Override
    public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName, JoinType jt) {
        return null;
    }

    @Override
    public Set<Fetch<T, ?>> getFetches() {
        return Set.of();
    }

    @Override
    public <Y> Fetch<T, Y> fetch(SingularAttribute<? super T, Y> attribute) {
        return null;
    }

    @Override
    public <Y> Fetch<T, Y> fetch(SingularAttribute<? super T, Y> attribute, JoinType jt) {
        return null;
    }

    @Override
    public <Y> Fetch<T, Y> fetch(PluralAttribute<? super T, ?, Y> attribute) {
        return null;
    }

    @Override
    public <Y> Fetch<T, Y> fetch(PluralAttribute<? super T, ?, Y> attribute, JoinType jt) {
        return null;
    }

    @Override
    public <X, Y> Fetch<X, Y> fetch(String attributeName) {
        return null;
    }

    @Override
    public <X, Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
        return null;
    }

    @Override
    public Predicate isNull() {
        return null;
    }

    @Override
    public Predicate isNotNull() {
        return null;
    }

    @Override
    public Predicate in(Object... values) {
        return null;
    }

    @Override
    public Predicate in(Expression<?>... values) {
        return null;
    }

    @Override
    public Predicate in(Collection<?> values) {
        return null;
    }

    @Override
    public Predicate in(Expression<Collection<?>> values) {
        return null;
    }

    @Override
    public <X> Expression<X> as(Class<X> type) {
        return null;
    }

    @Override
    public Selection<T> alias(String name) {
        return null;
    }

    @Override
    public boolean isCompoundSelection() {
        return false;
    }

    @Override
    public List<Selection<?>> getCompoundSelectionItems() {
        return List.of();
    }

    @Override
    public Class<? extends T> getJavaType() {
        return null;
    }

    @Override
    public String getAlias() {
        return "";
    }
}