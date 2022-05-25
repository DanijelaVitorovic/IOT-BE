package com.dex.coreserver.service;

import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.model.AbstractDataModel;
import com.dex.coreserver.model.enums.Actions;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.List;

public interface BasicService<T extends AbstractDataModel> {

    @Transactional
    T create(final T entity, final String username) throws AppException;
    @Transactional
    T update(final T entity, final String username) throws AppException;
    @Transactional
    void delete(final Long id, final String username) throws AppException;
    List<T> findAll(String username);
    T findById(final Long id);
    Page findAllByPageAndSize(int pageNumber, int pageSize);
    String getTableName(Class<T> tClass);
    List<Actions> getAllowedActions(String username, Long id);
}
