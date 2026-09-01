package com.example.COLLABORATION_SERVICE.controller;

import com.example.COLLABORATION_SERVICE.dto.ApiResponse;
import com.example.COLLABORATION_SERVICE.dto.ChatMessageResponse;
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
import com.example.COLLABORATION_SERVICE.service.ChatService;
import com.example.COLLABORATION_SERVICE.utils.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/collaboration/conversations")
@RequiredArgsConstructor
public class ConversationMessageController {

    private final ChatService chatService;

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<ApiResponse<PagedResponse<ChatMessageResponse>>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal,
            HttpServletRequest request
    ) {
        PagedResponse<ChatMessageResponse> messages =
                chatService.getConversationMessages(
                        conversationId,
                        page,
                        size,
                        principal
                );

        PagedResponse<ChatMessageResponse> response =
                PagedResponse.<ChatMessageResponse>builder()
                        .content(messages.getContent())
                        .size(messages.getSize())
                        .page(messages.getPage())
                        .first(messages.isFirst())
                        .last(messages.isLast())
                        .totalElements(messages.getTotalElements())
                        .totalPages(messages.getTotalPages())
                        .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<ChatMessageResponse>>builder()
                        .success(true)
                        .message("Messages fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }
}
