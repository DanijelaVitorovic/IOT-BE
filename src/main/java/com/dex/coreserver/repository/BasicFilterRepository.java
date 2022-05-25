package com.dex.coreserver.repository;

import com.dex.coreserver.model.AbstractDataModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface BasicFilterRepository <T extends AbstractDataModel,ID extends Serializable> extends JpaSpecificationExecutor<T>, JpaRepository<T,ID> {

}

