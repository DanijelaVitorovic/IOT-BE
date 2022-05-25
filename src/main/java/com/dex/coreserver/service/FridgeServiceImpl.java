package com.dex.coreserver.service;

import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.model.Fridge;
import com.dex.coreserver.model.User;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.repository.FridgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class FridgeServiceImpl extends BasicServiceImpl<Fridge> implements FridgeService {

    @Autowired
    private FridgeRepository fridgeRepository;

    @Override
    protected JpaRepository<Fridge, Long> getRepository() { return fridgeRepository; }

    @Override
    protected Actions getCreateAction() { return Actions.FRIDGE_CREATE; }

    @Override
    protected Actions getDeleteAction() { return Actions.FRIDGE_DELETE; }

    @Override
    protected Actions getUpdateAction() { return Actions.FRIDGE_UPDATE; }

    @Override
    protected Actions getFindAllAction() { return Actions.FRIDGE_FIND_ALL; }

    @Override
    protected void validate(Fridge entity, User user) throws AppException {

    }

    @Override
    protected void validatePersistenceException(DataAccessException e) throws AppException, DataIntegrityViolationException {

    }
}
