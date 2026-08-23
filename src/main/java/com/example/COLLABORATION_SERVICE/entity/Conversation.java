package com.example.COLLABORATION_SERVICE.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "conversations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_participants",
                        columnNames = {
                                "participant_one_id",
                                "participant_two_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;
}
