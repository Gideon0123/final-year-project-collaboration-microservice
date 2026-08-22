package com.example.COLLABORATION_SERVICE.repository;

import com.example.COLLABORATION_SERVICE.entity.CollaborationRequest;
import com.example.COLLABORATION_SERVICE.enums.CollaborationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaborationRequestRepository
        extends JpaRepository<CollaborationRequest, Long> {

    boolean existsBySenderIdAndReceiverId(
            Long senderId,
            Long receiverId
    );

    Page<CollaborationRequest> findBySenderId(
            Long senderId,
            Pageable pageable
    );

    Page<CollaborationRequest> findByReceiverId(
            Long receiverId,
            Pageable pageable
    );

    Page<CollaborationRequest> findBySenderIdAndStatus(
            Long senderId,
            CollaborationStatus status,
            Pageable pageable
    );

    Page<CollaborationRequest> findByReceiverIdAndStatus(
            Long receiverId,
            CollaborationStatus status,
            Pageable pageable
    );

    boolean existsBySenderIdAndReceiverIdAndStatus(
            Long senderId,
            Long receiverId,
            CollaborationStatus status
    );
}
