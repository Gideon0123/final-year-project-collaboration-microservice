package com.example.COLLABORATION_SERVICE.entity;

import com.example.COLLABORATION_SERVICE.enums.CollaborationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "collaboration_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CollaborationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long receiverId;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    private CollaborationStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}