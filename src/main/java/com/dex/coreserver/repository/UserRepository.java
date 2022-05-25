package com.dex.coreserver.repository;

import com.dex.coreserver.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends BasicFilterRepository<User,Long> {

    @Override
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    User findByUsername(String username);
    User getById(Long id);

    @Query("SELECT DISTINCT(u) FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAll();

    boolean existsByUsername(String username);

}
