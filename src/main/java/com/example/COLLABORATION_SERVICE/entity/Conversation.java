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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean groupChat;

    @Column(
            name = "participant_one_id",
            nullable = false
    )
    private Long participantOneId;

    @Column(
            name = "participant_two_id",
            nullable = false
    )
    private Long participantTwoId;

    private LocalDateTime lastMessageAt;

    @Enumerated(EnumType.STRING)
    private ConversationStatus status;
}
