package com.example.COLLABORATION_SERVICE.repository;

import com.example.COLLABORATION_SERVICE.entity.CollaborationConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaborationConnectionRepository
        extends JpaRepository<CollaborationConnection, Long> {

    boolean existsByUserOneIdAndUserTwoId(
            Long userOneId,
            Long userTwoId
    );
}