package com.example.COLLABORATION_SERVICE.controller;

import com.example.COLLABORATION_SERVICE.dto.ApiResponse;
import com.example.COLLABORATION_SERVICE.dto.CollaborationRequestResponse;
import com.example.COLLABORATION_SERVICE.dto.ConnectionResponse;
import com.example.COLLABORATION_SERVICE.dto.SendRequestDto;
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
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
            @Valid @RequestBody SendRequestDto dto,
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

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<PagedResponse<CollaborationRequestResponse>>> getSentRequests(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<CollaborationRequestResponse> Collaboration =
                collaborationService.getSentRequests(
                        userId, adjustedPage, size, sortBy, sortDirection
                );

        PagedResponse<CollaborationRequestResponse> response =
                PagedResponse.<CollaborationRequestResponse>builder()
                        .content(Collaboration.getContent())
                        .size(Collaboration.getSize())
                        .page(Collaboration.getPage())
                        .first(Collaboration.isFirst())
                        .last(Collaboration.isLast())
                        .totalElements(Collaboration.getTotalElements())
                        .totalPages(Collaboration.getTotalPages())
                        .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<CollaborationRequestResponse>>builder()
                        .success(true)
                        .message("Collaboration Requests fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<PagedResponse<CollaborationRequestResponse>>> getReceivedRequests(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<CollaborationRequestResponse> Collaboration =
                collaborationService.getReceivedRequests(
                        userId, adjustedPage, size, sortBy, sortDirection
                );

        PagedResponse<CollaborationRequestResponse> response =
                PagedResponse.<CollaborationRequestResponse>builder()
                        .content(Collaboration.getContent())
                        .size(Collaboration.getSize())
                        .page(Collaboration.getPage())
                        .first(Collaboration.isFirst())
                        .last(Collaboration.isLast())
                        .totalElements(Collaboration.getTotalElements())
                        .totalPages(Collaboration.getTotalPages())
                        .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<CollaborationRequestResponse>>builder()
                        .success(true)
                        .message("Collaboration Received Requests fetched successfully")
                        .status(HttpStatus.OK.value())
                        .data(response)
                        .errors(null)
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<ApiResponse<Void>> cancelRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-USER-ID") Long currentUserId,
            HttpServletRequest request
    ) {
        collaborationService.cancelRequest(
                requestId,
                currentUserId
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Request Canceled successfully")
                        .status(HttpStatus.OK.value())
                        .path(request.getRequestURI())
                        .traceId(TraceIdUtil.generate())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/connections")
    public ResponseEntity<ApiResponse<PagedResponse<ConnectionResponse>>> getConnections(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
//            @RequestParam String sortBy,
//            @RequestParam String sortDirection,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request
    ) {
        int adjustedPage = Math.max(page - 1, 0);
        PagedResponse<ConnectionResponse> Collaboration =
                collaborationService.getConnections(
                        userId, adjustedPage, size
                );

        PagedResponse<ConnectionResponse> response =
                PagedResponse.<ConnectionResponse>builder()
                        .content(Collaboration.getContent())
                        .size(Collaboration.getSize())
                        .page(Collaboration.getPage())
                        .first(Collaboration.isFirst())
                        .last(Collaboration.isLast())
                        .totalElements(Collaboration.getTotalElements())
                        .totalPages(Collaboration.getTotalPages())
                        .build();

        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<ConnectionResponse>>builder()
                        .success(true)
                        .message("Connections fetched successfully")
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