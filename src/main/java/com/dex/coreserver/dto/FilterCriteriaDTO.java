package com.dex.coreserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterCriteriaDTO {
    private String columnName; // isColumnFromOtherEntity => columnName = entity.columnName
    private Object columnValue;

    private String logicalOperator;

    private double comparatorValue; // logicalOperator == ">" || "<" || "<=" || ">="

    private double comparatorValueMin; // logicalOperator == "><"
    private double comparatorValueMax; // logicalOperator == "><"

    private Date dateMin; // logicalOperator == "><"
    private Date dateMax;

    private boolean returnAllSignal;
}
