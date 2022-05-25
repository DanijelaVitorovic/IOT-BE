package com.dex.coreserver.repository;
import com.dex.coreserver.model.FilterModel;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepository extends BasicFilterRepository <FilterModel,Long> {

}
