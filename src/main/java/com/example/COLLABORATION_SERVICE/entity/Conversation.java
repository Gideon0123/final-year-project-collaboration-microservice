package com.example.COLLABORATION_SERVICE.entity;

import com.example.COLLABORATION_SERVICE.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation extends BaseEntity {

    private String name;

    private boolean groupChat;

    @ElementCollection
    @CollectionTable(
            name = "conversation_participants",
            joinColumns = @JoinColumn(name = "conversation_id")
    )
    @Column(name = "user_id")
    private Set<Long> participants = new HashSet<>();

    private LocalDateTime lastMessageAt;

    @Enumerated(EnumType.STRING)
    private ConversationStatus status;
}
