package com.dex.coreserver.service;

import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.model.filter.SearchQuery;
import com.dex.coreserver.repository.BasicFilterRepository;
import com.dex.coreserver.util.Constants;
import com.dex.coreserver.util.SpecificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

@Service
public class FilterServiceImpl implements FilterService {

    @Autowired
    private BasicFilterRepository basicFilterRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Override
    public ResponseEntity<?> getResultByCriteria(SearchQuery searchQuery) {
        Class tClass = getClassByClassName(searchQuery.getClassName());
        PageRequest pageRequest = getPageRequest(searchQuery);
        Page page = getResult(tClass,searchQuery,pageRequest);
        return new ResponseEntity<>(page,HttpStatus.OK);
    }

    private Page<?> getResult(Class domainClass, SearchQuery searchQuery, PageRequest pageRequest) {
        try{
            Repositories repositories = new Repositories(webApplicationContext);
            basicFilterRepository = (BasicFilterRepository) repositories.getRepositoryFor(domainClass).orElseThrow(()->
                    new IllegalStateException("UNKNOWN_EXCEPTION"));
            return basicFilterRepository.findAll(SpecificationUtil.bySearchQuery(searchQuery), pageRequest);
        } catch (Exception e){
            throw new AppException("UNKNOWN_EXCEPTION");
        }
    }

    private Class getClassByClassName(String className) {
        Class tClass;
        try {
            tClass = Class.forName(Constants.PACKAGE_MODEL +className);
        } catch (ClassNotFoundException e) {
            throw new AppException("CLASS_NAME_NOT_FOUND");
        }
        return tClass;
    }

    private PageRequest getPageRequest(SearchQuery searchQuery) {

        int pageNumber = searchQuery.getPageNumber();
        int pageSize = searchQuery.getPageSize();

        List<Sort.Order> orders = new ArrayList<>();
        List<String> ascProps = searchQuery.getSortOrder().getAscendingOrder();

        if (ascProps != null && !ascProps.isEmpty()) {
            for (String prop : ascProps) {
                orders.add(Sort.Order.asc(prop));
            }
        }

        List<String> descProps = searchQuery.getSortOrder().getDescendingOrder();
        if (descProps != null && !descProps.isEmpty()) {
            for (String prop : descProps) {
                orders.add(Sort.Order.desc(prop));
            }
        }

        Sort sort = Sort.by(orders);
        return PageRequest.of(pageNumber, pageSize, sort);

    }
}
