package com.example.COLLABORAION_SERVICE.repository;

import com.example.COLLABORAION_SERVICE.entity.CollaborationConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaborationConnectionRepository
        extends JpaRepository<CollaborationConnection, Long> {
}