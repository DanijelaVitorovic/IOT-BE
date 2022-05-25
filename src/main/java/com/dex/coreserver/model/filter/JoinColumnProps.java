package com.dex.coreserver.model.filter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinColumnProps {

    private String joinColumnName;
    private SearchFilter searchFilter;

}
