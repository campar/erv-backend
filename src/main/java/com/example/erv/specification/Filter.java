package com.example.erv.specification;


import jakarta.persistence.criteria.JoinType;
import lombok.Getter;

@Getter
public class Filter {
    private final String key;
    private final SearchOperator operator;
    private final Object value;
    private String joinEntity;
    private JoinType joinType;

    public Filter(String key, SearchOperator operator, Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public Filter(String key, Object value) {
        this.key = key;
        this.operator = SearchOperator.EQUAL;
        this.value = value;
    }

    public Filter(String joinEntity, JoinType joinType, String key, SearchOperator operator, Object value) {
        this.joinEntity = joinEntity;
        this.key = key;
        this.operator = operator;
        this.value = value;
        this.joinType = joinType;
    }

    public Filter(String joinEntity, String key, SearchOperator operator, Object value) {
        this.joinEntity = joinEntity;
        this.joinType = JoinType.INNER;
        this.key = key;
        this.operator = operator;
        this.value = value;
    }
}
