package com.example.COLLABORAION_SERVICE.repository;

import com.example.COLLABORAION_SERVICE.entity.CollaborationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaborationRequestRepository
        extends JpaRepository<CollaborationRequest, Long> {

    Page<CollaborationRequest> findBySenderId(
            Long senderId,
            Pageable pageable
    );

    Page<CollaborationRequest> findByReceiverId(
            Long receiverId,
            Pageable pageable
    );
}
