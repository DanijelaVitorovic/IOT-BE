package com.dex.coreserver.repository;

import com.dex.coreserver.model.Fridge;
import org.springframework.stereotype.Repository;

@Repository
public interface FridgeRepository extends BasicFilterRepository<Fridge, Long> {
}
