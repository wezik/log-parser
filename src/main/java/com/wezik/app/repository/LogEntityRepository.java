package com.wezik.app.repository;

import com.wezik.app.domain.FullLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogEntityRepository extends CrudRepository<FullLog,String> {
    @Query
    Long countFlagged();
    Optional<FullLog> findById(String id);
}
