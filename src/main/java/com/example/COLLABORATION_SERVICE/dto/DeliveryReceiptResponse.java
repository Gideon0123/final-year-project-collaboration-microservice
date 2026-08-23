package com.example.COLLABORATION_SERVICE.dto;

import com.example.COLLABORATION_SERVICE.enums.MessageStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReceiptResponse {

    private Long messageId;
    private MessageStatus status;
    private Long receiverId;
}
