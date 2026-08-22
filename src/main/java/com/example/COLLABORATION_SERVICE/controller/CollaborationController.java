package com.example.COLLABORATION_SERVICE.controller;

import com.example.COLLABORATION_SERVICE.dto.ApiResponse;
import com.example.COLLABORATION_SERVICE.dto.CollaborationRequestResponse;
import com.example.COLLABORATION_SERVICE.dto.SendRequestDto;
import com.example.COLLABORATION_SERVICE.service.CollaborationService;
import com.example.COLLABORATION_SERVICE.utils.TraceIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/collaboration")
public class CollaborationController {

    private final CollaborationService collaborationService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<CollaborationRequestResponse>> sendRequest(
            @RequestHeader("X-USER-ID") Long senderId,
            @Valid SendRequestDto dto,
            HttpServletRequest request
    ) {
        CollaborationRequestResponse response = collaborationService.sendRequest(senderId, dto);

        return ResponseEntity.ok(
                ApiResponse.<CollaborationRequestResponse>builder()
                        .success(true)
                        .message("Collaboration Request Sent successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<ApiResponse<CollaborationRequestResponse>> acceptRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-USER-ID") Long currentUserId,
            HttpServletRequest request
    ) {
        CollaborationRequestResponse response = collaborationService.acceptRequest(
                requestId, currentUserId
        );

        return ResponseEntity.ok(
                ApiResponse.<CollaborationRequestResponse>builder()
                        .success(true)
                        .message("Collaboration Request Accepted")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<CollaborationRequestResponse>> rejectRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-USER-ID") Long currentUserId,
            HttpServletRequest request
    ) {
        CollaborationRequestResponse response = collaborationService.rejectRequest(
                requestId, currentUserId
        );

        return ResponseEntity.ok(
                ApiResponse.<CollaborationRequestResponse>builder()
                        .success(true)
                        .message("Collaboration Request Rejected")
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