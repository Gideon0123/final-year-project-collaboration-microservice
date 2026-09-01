package com.example.COLLABORATION_SERVICE.repository;

import com.example.COLLABORATION_SERVICE.entity.Message;
import com.example.COLLABORATION_SERVICE.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByConversationId(
            Long conversationId,
            Pageable pageable
    );

    List<Message> findByConversationIdOrderByCreatedAtAsc(
            Long conversationId
    );

    List<Message> findByReceiverIdAndStatus(
            Long receiverId,
            MessageStatus status
    );

}