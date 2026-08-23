package com.example.COLLABORATION_SERVICE.repository;

import com.example.COLLABORATION_SERVICE.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation>
    findByParticipantOneIdAndParticipantTwoId(
            Long participantOneId,
            Long participantTwoId
    );
}