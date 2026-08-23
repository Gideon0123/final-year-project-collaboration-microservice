package com.example.COLLABORATION_SERVICE.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingEvent {

    private Long senderId;
    private Long receiverId;
    private boolean typing;
}
