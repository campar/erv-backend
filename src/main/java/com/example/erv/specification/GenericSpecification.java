package com.example.erv.specification;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.erv.config.MapperConfig;
import com.example.erv.exceptions.ErvException;
import com.example.erv.utils.LocalDateTimeParser;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GenericSpecification<T> implements Specification<T> {
    private static final Logger logger = LogManager.getLogger(GenericSpecification.class);
    @Serial
    private static final long serialVersionUID = 1L;
    private final transient Filter filter;

    public GenericSpecification(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        if (filter.getJoinEntity() == null && filter.getOperator() != SearchOperator.BETWEEN && (filter.getOperator() == SearchOperator.IN || filter.getValue() instanceof Collection<?>)) {
            return handleCollectionValue(root, builder);
        }
        if (filter.getJoinEntity() != null) {
            return handleJoinValue(root, builder, filter.getJoinEntity(), filter.getJoinType());
        }

        if (isForeignKey(filter, root)) {
            try {
                return handleFKStructure(root, builder);
            } catch (ErvException e) {
                logger.error(e);
                return null;
            }
        }

        try {
            return handleSingleValue(root, builder);
        } catch (ClassCastException e) {
            return null;
        }
    }

    private boolean isForeignKey(Filter filter, Root<T> root) {
        try {
            if (filter.getKey() == null || filter.getKey().isBlank() || root.getJavaType() == null) return false;

            Class<?> entityClass = root.getJavaType();

            // Try to get the field by the attribute name
            Field field = entityClass.getDeclaredField(filter.getKey());

            // Check if the field is annotated with a JPA relationship annotation
            return field.isAnnotationPresent(ManyToOne.class) ||
                    field.isAnnotationPresent(OneToMany.class) ||
                    field.isAnnotationPresent(OneToOne.class) ||
                    field.isAnnotationPresent(ManyToMany.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    @SuppressWarnings({"rawtypes"})
    private Predicate handleSingleValue(Root<T> root, CriteriaBuilder builder) {
        if (filter.getValue() instanceof Collection && filter.getOperator() == SearchOperator.BETWEEN) {
            return buildBetweenPredicate(root, filter.getValue(), builder, filter.getKey());
        }

        Comparable value = (Comparable) filter.getValue();
        return buildPredicate(root, value, builder, filter.getKey());
    }

    @SuppressWarnings("rawtypes")
    private Predicate handleFKStructure(Root<T> root, CriteriaBuilder builder) throws ErvException {
        if (filter.getOperator() == SearchOperator.BETWEEN) {
            return buildBetweenPredicate(root, filter.getValue(), builder, filter.getKey());
        }

        if (filter.getValue() == null) {
            return buildPredicate(root, null, builder, filter.getKey());
        }

        Map<String, Object> map = parseNestedObject();
        cleanMap(map);
        if (map.containsKey("id") && map.get("id") != null) {
            return buildPredicate(root.get(filter.getKey()), (Comparable) map.get("id"), builder, "id");
        }
        Join<?, T> join = root.join(filter.getKey());
        List<Predicate> predicates = new ArrayList<>(List.of(builder.isTrue(builder.literal(true))));
        addPredicatesFromMap(builder, map, predicates, join);
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseNestedObject() throws ErvException {
        try {
            return MapperConfig.getObjectMapper().registerModule(new JavaTimeModule()).convertValue(filter.getValue(), Map.class);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ErvException("Cannot cast nested object: " + filter.getKey() + ": " + filter.getValue());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addPredicatesFromMap(CriteriaBuilder builder, Map<String, Object> map, List<Predicate> predicates, Join<?, ?> join) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null) continue;
            if (entry.getValue() instanceof Map) {
                predicates.add(handleNestedMap(join.join(entry.getKey()), (Map<String, Object>) entry.getValue(), builder));
            } else {
                predicates.add(buildPredicate(join, (Comparable) entry.getValue(), builder, entry.getKey()));
            }
        }
    }

    private Predicate handleNestedMap(Join<?, ?> join, Map<String, Object> map, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        addPredicatesFromMap(builder, map, predicates, join);
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    @SuppressWarnings({"rawtypes"})
    private Predicate handleJoinValue(Root<T> root, CriteriaBuilder builder, String joinEntity, JoinType joinType) {
        Join<?, ?> join = root.join(joinEntity, joinType);

        if (filter.getOperator() == SearchOperator.BETWEEN) {
            return buildBetweenPredicate(join, filter.getValue(), builder, filter.getKey());
        }
        if (filter.getOperator() == SearchOperator.IN) {
            Root<T> joinRoot = new JoinRoot<>(root.join(joinEntity, joinType));
            return handleCollectionValue(joinRoot, builder);
        }
        return buildPredicate(join, (Comparable) filter.getValue(), builder, filter.getKey());
    }

    @SuppressWarnings("unchecked")
    private Predicate handleCollectionValue(Root<T> root, CriteriaBuilder builder) {
        /*
         * TODO ovo nije podrzano trenutno...
         *     @ManyToMany
         *     @JoinTable(name = "user_shift", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "shift_id"))
         *     private Set<Shift> shifts = new LinkedHashSet<>();
         */

        Collection<?> values = (Collection<?>) filter.getValue();
        CriteriaBuilder.In<Object> inClause = builder.in(root.get(filter.getKey()));
        for (Object item : values) {
            try {
                inClause.value(item);
            } catch (Exception e) {
                Class<?> type = getFieldType(root);
                T entity = (T) MapperConfig.getObjectMapper().registerModule(new JavaTimeModule()).convertValue(item, type);
                inClause.value(entity);
            }
        }

        return inClause;
    }

    private Class<?> getFieldType(Root<T> root) {
        try {
            if (hasGenericClass(root.getJavaType(), filter.getKey())) {
                return getGenericClass(root.getJavaType().getDeclaredField(filter.getKey()));
            }
            return root.getJavaType().getDeclaredField(filter.getKey()).getType();
        } catch (NoSuchFieldException e) {
            return filter.getValue().getClass();
        }
    }

    private boolean hasGenericClass(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.getGenericType() instanceof ParameterizedType;
        } catch (NoSuchFieldException e) {
            logger.warn("Generic class not found.", e);
            return false;
        }
    }

    private Class<?> getGenericClass(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        if (actualTypeArguments[0] instanceof Class<?> clazz) {
            return clazz;
        } else {
            return null;
        }
    }

    private void cleanMap(Map<String, Object> map) {
        map.entrySet().forEach(e -> {
            if (e.getValue() != null && LocalDateTimeParser.matches(e.getValue().toString()))
                e.setValue(LocalDateTimeParser.parse(e.getValue().toString()));
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildPredicate(Path<?> root, Comparable value, CriteriaBuilder builder, String key) {
        switch (filter.getOperator()) {
            case GTE:
                return builder.greaterThanOrEqualTo(root.get(key), value);
            case GT:
                return builder.greaterThan(root.get(key), value);
            case LTE:
                return builder.lessThanOrEqualTo(root.get(key), value);
            case LT:
                return builder.lessThan(root.get(key), value);
            case NOT_EQUAL:
                if (value == null) {
                    return builder.isNotNull(root.get(key));
                }
                return builder.notEqual(root.get(key), value);
            case EQUAL:
                if (value == null) {
                    return builder.isNull(root.get(key));
                }
                return builder.equal(root.get(key), value);
            case STARTS_WITH:
                if (root.get(key).getJavaType() == String.class) {
                    return builder.like(root.get(key), value + "%");
                } else {
                    return builder.equal(root.get(key), value);
                }
            case ENDS_WITH:
                if (root.get(key).getJavaType() == String.class) {
                    return builder.like(root.get(key), "%" + value);
                } else {
                    return builder.equal(root.get(key), value);
                }
            case LIKE:
                if (root.get(key).getJavaType() == String.class) {
                    return builder.like(root.get(key), "%" + value + "%");
                } else {
                    return builder.equal(root.get(key), value);
                }
            case LIKE_IGNORE_CASE:
                if (root.get(key).getJavaType() == String.class) {
                    return builder.like(builder.upper(root.get(key)), "%" + value.toString().toUpperCase() + "%");
                } else {
                    return builder.equal(builder.upper(root.get(key)), "%" + value.toString().toUpperCase() + "%");
                }
            case BETWEEN:
                return buildBetweenPredicate(root, value, builder, key);
            default:
                return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Predicate buildBetweenPredicate(Path<?> root, Object value, CriteriaBuilder builder, String key) {
        if (!(value instanceof List<?> range) || range.size() != 2) return null;
        if (range.get(0) != null && range.get(1) != null) {
            return builder.between(root.get(key), (Comparable) range.get(0), (Comparable) range.get(1));
        } else if (range.get(0) != null && range.get(1) == null) {
            return builder.greaterThanOrEqualTo(root.get(key), (Comparable) range.get(0));
        } else if (range.get(0) == null && range.get(1) != null) {
            return builder.lessThanOrEqualTo(root.get(key), (Comparable) range.get(1));
        }
        return null;
    }
}
