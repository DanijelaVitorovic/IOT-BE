package com.dex.coreserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {
    private String tableName;
    private FilterCriteriaDTO[] filterCriteria;
}
