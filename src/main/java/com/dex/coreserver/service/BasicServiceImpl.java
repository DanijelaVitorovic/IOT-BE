package com.dex.coreserver.service;

import com.dex.coreserver.exceptions.*;
import com.dex.coreserver.model.AbstractDataModel;
import com.dex.coreserver.model.User;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import java.util.List;
import java.util.Optional;

@Service
public abstract class BasicServiceImpl<T extends AbstractDataModel> implements BasicService<T> {

    abstract protected JpaRepository<T, Long> getRepository();

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public T create(final T entity, final String username) throws AppException {
        User readUser = readUser(username);
        validatePrerequisite(true, checkPermissionForUser(getCreateAction(), readUser));
        return store(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public T update(final T entity, final String username) {
        User readUser = readUser(username);
        validatePrerequisite(true, checkPermissionForUser(getUpdateAction(), readUser));
        return store(entity);
    }

    @Override
    @Transactional
    public void delete(final Long id, final String username) {
        User readUser = readUser(username);
        validatePrerequisite(true, checkPermissionForUser(getDeleteAction(), readUser));
        try {
            T entity = findById(id);
            getRepository().delete(entity);
            em.flush();
        } catch (DataAccessException e) {
            validatePersistence(e);
        } catch (Exception ex){
            if(ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException)
                throw new AppDataIntegrityViolationException("REFERENCED_KEY");
            else {
                GlobalExceptionHandler.globalExceptionHandler( ex );
            }
        } finally {
            em.close();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(final Long id) {
        Optional<T> entity = getRepository().findById(id);
        if(!entity.isPresent()){
            throw new AppResourceNotFoundException("ENTITY_NOT_FOUND");
        }
        return entity.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(String username) {
        User readUser = readUser(username);
        validatePrerequisite(true, checkPermissionForUser(getFindAllAction(), readUser));
        List<T> list = getRepository().findAll();
        if(list.isEmpty()){
            throw new AppNoDataFoundException("EMPTY_LIST");
        }
        return list;
    }

    @Override
    public Page findAllByPageAndSize(int pageNumber, int pageSize) {
        return getRepository().findAll(PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending()));
    }

    @Transactional
    private T store(final T entity) {
        T t = null;
        try {
            t = getRepository().save(entity);
        } catch (DataAccessException e) {
            validatePersistence(e);
        }
        finally {
            em.close();
        }
        return t;
    }

    protected abstract Actions getCreateAction();

    protected abstract Actions getDeleteAction();

    protected abstract Actions getUpdateAction();

    protected abstract Actions getFindAllAction();

    protected T readExisting(final T entity, final User user) throws AppException {
        validateParameterIsNotNull(entity);
        validateIdIsNotNull(entity.getId());
        return findById(entity.getId());
    }

    protected User readUser(String username) throws AppException {
        Validate.isNotNull(username,"USER_IS_NULL");
        User readUser = userService.findByUsername(username);
        Validate.isNotNull(readUser,"USER_DOES_NOT_EXIST_FOR_USERNAME");
        return readUser;
    }

    private void validatePersistence(DataAccessException e) throws AppException, DataIntegrityViolationException {
        if(e.getMessage() == null)
            throw new AppException("UNKNOWN_EXCEPTION");
        if (e.getMessage().indexOf("OptimisticLockException") > 0) {
            throw new AppException("OPTIMISTIC_LOCK");
        }
        if (e.getMessage().indexOf("child record found") > 0) {
            throw new AppException("REFERENCED_KEY");
        }
        if (e.getMessage().indexOf("value too large for column") > 0) {
            throw new AppException("VALUE_TOO_LARGE_FOR_COLUMN");
        }
        if (e.getMessage().indexOf("duplicate key value") > 0) {
            throw new AppException("PRIMARY_KEY_DUPLICATED");
        }

        validatePersistenceException(e);
    }

    protected void validateParameterIsNotNull(Object parameter) throws AppException {
        Validate.isNotNull(parameter,"PARAMETER_IS_NULL");
    }

    protected void validateIdIsNotNull(Long id) throws AppException {
        Validate.isNotNull(id,"ID_IS_NULL");

    }

    protected boolean checkPermissionForUser(Actions action, User readUser) {
        return Validate.checkUserPermission(action, readUser);
    }

    abstract protected void validate(final T entity, final User user) throws AppException;

    abstract protected void validatePersistenceException(DataAccessException e) throws AppException, DataIntegrityViolationException;

    protected void validatePrerequisite(boolean prerequisite, boolean permissionPrerequisite) throws AppException {
        if (!permissionPrerequisite) {
            throw new AppException("ACTION_IS_NOT_ALLOWED");
        }
        if (!prerequisite) {
            throw new AppException("ILLEGAL_OPERATION");
        }

    }

    @Override
    public String getTableName(Class<T> tClass) {
        Table table = tClass.getAnnotation(Table.class);
        return table.name();
    }

    @Override
    public List<Actions> getAllowedActions(String username, Long id) {
        return null;
    }
}
