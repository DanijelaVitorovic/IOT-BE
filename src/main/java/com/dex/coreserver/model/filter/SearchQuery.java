package com.dex.coreserver.model.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchQuery {

    private String className;
    private List<SearchFilter> searchFilter;
    private int pageNumber;
    private int pageSize;
    private SortOrder sortOrder;
    private List<JoinColumnProps> joinColumnProps;

}
