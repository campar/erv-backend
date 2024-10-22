package com.example.erv.specification;

import java.util.List;

import lombok.Data;

@Data
public class SearchFilter {
    private String key;
    private SearchOperator searchOperator;
    private Object value;
    private LogicalOperator logicalOperator;
    private List<SearchFilter> filters;
}
