package com.example.erv.specification;

import java.util.List;

import lombok.Data;

@Data
public class FilterGroup {
    private LogicalOperator logicalOperator;
    private List<SearchFilter> filters;
}
