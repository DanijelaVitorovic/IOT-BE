package com.dex.coreserver.service;

import com.dex.coreserver.model.filter.SearchQuery;
import org.springframework.http.ResponseEntity;

public interface FilterService {

    ResponseEntity<?> getResultByCriteria(SearchQuery searchQuery);
}
