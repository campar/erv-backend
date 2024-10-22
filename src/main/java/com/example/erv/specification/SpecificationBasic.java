package com.example.erv.specification;

import com.example.erv.service.EntityAttribute;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;

@Getter
public class SpecificationBasic<T> {
    private static final Logger logger = LogManager.getLogger(SpecificationBasic.class);

    private Specification<T> specification;
    private boolean distinct = false;

    protected SpecificationBasic(Filter filter) {
        this.specification = Specification.where(new GenericSpecification<>(filter));
    }
    
    public static <T> SpecificationBasic<T> get() {
        return new SpecificationBasic<>(new Filter("", SearchOperator.EMPTY_FILTER, null));
    }

    public static <T> SpecificationBasic<T> where(EntityAttribute key, SearchOperator operation, Object value) {
        return new SpecificationBasic<>(new Filter(key.getName(), operation, value));
    }

    public static <T> SpecificationBasic<T> where(EntityAttribute key, Object value) {
        return new SpecificationBasic<>(new Filter(key.getName(), value));
    }

    public SpecificationBasic<T> and(EntityAttribute key, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(key.getName(), value)));
        return this;
    }

    public SpecificationBasic<T> and(Specification<T> specification) {
        this.specification = specification.and(this.specification);
        return this;
    }

    public SpecificationBasic<T> and(EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(key.getName(), operation, value)));
        return this;
    }

    public SpecificationBasic<T> and(SpecificationBasic<T> spec) {
        specification = specification.and(spec.getSpecification());
        return this;
    }

    public SpecificationBasic<T> or(EntityAttribute key, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(key.getName(), value)));
        return this;
    }

    public SpecificationBasic<T> or(EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(key.getName(), operation, value)));
        return this;
    }

    public SpecificationBasic<T> or(SpecificationBasic<T> spec) {
        specification = specification.or(spec.getSpecification());
        return this;
    }

    public SpecificationBasic<T> joinAnd(EntityAttribute joinEntity, EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(joinEntity.getName(), key.getName(), operation, value)));
        return this;
    }

    public <X> SpecificationBasic<T> joinAnd(EntityAttribute joinEntity, SpecificationBasic<X> joinSpec) {
        Specification<T> innerJoinSpec = getJoinedSpecification(joinEntity.getName(), JoinType.INNER, joinSpec);
        specification = specification.and(innerJoinSpec);
        return this;
    }

    public SpecificationBasic<T> joinAnd(EntityAttribute joinEntity, EntityAttribute key, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(joinEntity.getName(), key.getName(), SearchOperator.EQUAL, value)));
        return this;
    }

    public SpecificationBasic<T> leftJoinAnd(EntityAttribute joinEntity, EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.LEFT, key.getName(), operation, value)));
        return this;
    }

    public <X> SpecificationBasic<T> leftJoinAnd(EntityAttribute joinEntity, SpecificationBasic<X> joinSpec) {
        Specification<T> leftJoinSpec = getJoinedSpecification(joinEntity.getName(), JoinType.LEFT, joinSpec);
        specification = specification.and(leftJoinSpec);
        return this;
    }

    public SpecificationBasic<T> leftJoinAnd(EntityAttribute joinEntity, EntityAttribute key, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.LEFT, key.getName(), SearchOperator.EQUAL, value)));
        return this;
    }

    public SpecificationBasic<T> rightJoinAnd(EntityAttribute joinEntity, EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.RIGHT, key.getName(), operation, value)));
        return this;
    }

    public <X> SpecificationBasic<T> rightJoinAnd(EntityAttribute joinEntity, SpecificationBasic<X> joinSpec) {
        Specification<T> rightJoinSpec = getJoinedSpecification(joinEntity.getName(), JoinType.RIGHT, joinSpec);
        specification = specification.and(rightJoinSpec);
        return this;
    }

    public SpecificationBasic<T> rightJoinAnd(EntityAttribute joinEntity, EntityAttribute key, Object value) {
        specification = specification.and(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.RIGHT, key.getName(), SearchOperator.EQUAL, value)));
        return this;
    }

    public <X> SpecificationBasic<T> joinOr(EntityAttribute joinEntity, SpecificationBasic<X> joinSpec) {
        Specification<T> innerJoinSpec = getJoinedSpecification(joinEntity.getName(), JoinType.INNER, joinSpec);
        specification = specification.or(innerJoinSpec);
        return this;
    }

    public SpecificationBasic<T> joinOr(EntityAttribute joinEntity, EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(joinEntity.getName(), key.getName(), operation, value)));
        return this;
    }

    public SpecificationBasic<T> joinOr(EntityAttribute joinEntity, EntityAttribute key, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(joinEntity.getName(), key.getName(), SearchOperator.EQUAL, value)));
        return this;
    }

    public <X> SpecificationBasic<T> leftJoinOr(EntityAttribute joinEntity, SpecificationBasic<X> joinSpec) {
        Specification<T> leftJoinSpec = getJoinedSpecification(joinEntity.getName(), JoinType.LEFT, joinSpec);
        specification = specification.or(leftJoinSpec);
        return this;
    }

    public SpecificationBasic<T> leftJoinOr(EntityAttribute joinEntity, EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.LEFT, key.getName(), operation, value)));
        return this;
    }

    public SpecificationBasic<T> leftJoinOr(EntityAttribute joinEntity, EntityAttribute key, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.LEFT, key.getName(), SearchOperator.EQUAL, value)));
        return this;
    }

    public <X> SpecificationBasic<T> rightJoinOr(EntityAttribute joinEntity, SpecificationBasic<X> joinSpec) {
        Specification<T> rightJoinSpec = getJoinedSpecification(joinEntity.getName(), JoinType.RIGHT, joinSpec);
        specification = specification.or(rightJoinSpec);
        return this;
    }

    public SpecificationBasic<T> rightJoinOr(EntityAttribute joinEntity, EntityAttribute key, SearchOperator operation, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.RIGHT, key.getName(), operation, value)));
        return this;
    }

    public SpecificationBasic<T> rightJoinOr(EntityAttribute joinEntity, EntityAttribute key, Object value) {
        specification = specification.or(new GenericSpecification<>(new Filter(joinEntity.getName(), JoinType.RIGHT, key.getName(), SearchOperator.EQUAL, value)));
        return this;
    }

    public SpecificationBasic<T> join(EntityAttribute key) {
        this.specification = this.specification.and((root, query, criteriaBuilder) -> {
            root.join(key.getName(), JoinType.LEFT);
            return criteriaBuilder.conjunction();
        });
        return this;
    }

    public SpecificationBasic<T> nestedJoinAnd(EntityAttribute rootJoin, EntityAttribute nestedJoin, EntityAttribute key, SearchOperator operation, Object value) {
        Specification<T> joinSpec = getNestedJoinSpecification(rootJoin, nestedJoin, JoinType.INNER, key, operation, value);
        specification = specification.and(joinSpec);
        return this;
    }

    public SpecificationBasic<T> nestedJoinOr(EntityAttribute rootJoin, EntityAttribute nestedJoin, EntityAttribute key, SearchOperator operation, Object value) {
        Specification<T> joinSpec = getNestedJoinSpecification(rootJoin, nestedJoin, JoinType.INNER, key, operation, value);
        specification = specification.or(joinSpec);
        return this;
    }

    public SpecificationBasic<T> nestedLeftJoinOr(EntityAttribute rootJoin, EntityAttribute nestedJoin, EntityAttribute key, SearchOperator operation, Object value) {
        Specification<T> joinSpec = getNestedJoinSpecification(rootJoin, nestedJoin, JoinType.LEFT, key, operation, value);
        specification = specification.or(joinSpec);
        return this;
    }

    private <X, Y> Specification<T> getNestedJoinSpecification(EntityAttribute rootJoin, EntityAttribute nestedJoin, JoinType joinType, EntityAttribute key, SearchOperator operation, Object value) {
        return (root, query, criteriaBuilder) -> {
            Join<X, Y> firstJoin = root.join(rootJoin.getName(), joinType); // First join
            Join<Y, ?> secondJoin = firstJoin.join(nestedJoin.getName(), joinType); // Nested join

            // Add the filter condition on the joined entity (secondJoin)
            return criteriaBuilder.equal(secondJoin.get(key.getName()), value);
        };
    }

    private static <T, X> Specification<T> getJoinedSpecification(String joinEntity, JoinType joinType, SpecificationBasic<X> joinSpec) {
        return (root, query, criteriaBuilder) -> {
            Root<X> rootX = new JoinRoot<>(root.join(joinEntity, joinType));
            return joinSpec.getSpecification().toPredicate(rootX, query, criteriaBuilder);
        };
    }

    public SpecificationBasic<T> distinct() {
        this.distinct = true;
        return this;
    }

    public Specification<T> getSpecification() {
        return (root, query, criteriaBuilder) -> {
            if (specification == null) {
                return criteriaBuilder.conjunction(); // Returns a predicate that is always true
            }
            if (distinct) {
                query.distinct(true);
            }
            // If the filter of the current specification is empty, return conjunction (always true)
            Specification<T> validSpec = removeEmptySpecifications(specification);

            // If the final specification is empty after filtering, return a default always-true predicate
            if (validSpec == null) {
                return criteriaBuilder.conjunction();
            }
            return specification.toPredicate(root, query, criteriaBuilder);
        };
    }

    private Specification<T> removeEmptySpecifications(Specification<T> spec) {
        // Filter logic that checks if the specification has a valid filter
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

            // Check if the filter produces a valid predicate, if not return null or conjunction
            if (predicate == null || predicate.getExpressions().isEmpty()) {
                return null;
            }

            return predicate;
        };
    }
}
