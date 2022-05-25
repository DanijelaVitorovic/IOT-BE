package com.dex.coreserver.controller;

import com.dex.coreserver.dto.FilterDTO;
import com.dex.coreserver.model.filter.SearchQuery;
import com.dex.coreserver.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;


@RestController
@RequestMapping("api/filter")
public class FilterController {

    @Autowired
    private FilterService filterService;

    @PostMapping(path = "/search")
    public ResponseEntity<?> getResultByCriteria(@RequestBody SearchQuery searchQuery) {
        return filterService.getResultByCriteria(searchQuery);
    }


}
