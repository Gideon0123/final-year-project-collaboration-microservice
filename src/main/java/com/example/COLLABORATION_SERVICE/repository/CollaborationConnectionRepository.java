package com.example.COLLABORATION_SERVICE.repository;

import com.example.COLLABORATION_SERVICE.entity.CollaborationConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaborationConnectionRepository
        extends JpaRepository<CollaborationConnection, Long> {

    boolean existsByUserOneIdAndUserTwoId(
            Long userOneId,
            Long userTwoId
    );

    Page<CollaborationConnection> findByUserOneId(
            Long userId,
            Pageable pageable
    );

    Page<CollaborationConnection> findByUserTwoId(
            Long userId,
            Pageable pageable
    );

}