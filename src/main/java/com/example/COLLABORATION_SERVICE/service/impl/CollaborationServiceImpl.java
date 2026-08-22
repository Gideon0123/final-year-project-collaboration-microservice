package com.example.COLLABORATION_SERVICE.service.impl;

import com.example.COLLABORATION_SERVICE.dto.*;
import com.example.COLLABORATION_SERVICE.entity.CollaborationConnection;
import com.example.COLLABORATION_SERVICE.entity.CollaborationRequest;
import com.example.COLLABORATION_SERVICE.enums.AccountStatus;
import com.example.COLLABORATION_SERVICE.enums.CollaborationStatus;
import com.example.COLLABORATION_SERVICE.enums.Role;
import com.example.COLLABORATION_SERVICE.exception.AccessDeniedException;
import com.example.COLLABORATION_SERVICE.exception.BadRequestException;
import com.example.COLLABORATION_SERVICE.exception.ResourceNotFoundException;
import com.example.COLLABORATION_SERVICE.feign.AuthClient;
import com.example.COLLABORATION_SERVICE.feign.ResearchClient;
import com.example.COLLABORATION_SERVICE.mapper.CollaborationMapper;
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
import com.example.COLLABORATION_SERVICE.publisher.CollaborationProducer;
import com.example.COLLABORATION_SERVICE.repository.CollaborationConnectionRepository;
import com.example.COLLABORATION_SERVICE.repository.CollaborationRequestRepository;
import com.example.COLLABORATION_SERVICE.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationServiceImpl implements CollaborationService {

    private final CollaborationRequestRepository requestRepository;
    private final CollaborationConnectionRepository connectionRepository;
    private final CollaborationProducer producer;
    private final CollaborationMapper mapper;
    private final AuthClient authClient;
    private final ResearchClient researchClient;

    private Pageable buildPageable(
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }



    @Transactional
    @Override
    public CollaborationRequestResponse sendRequest(
            Long senderId,
            SendRequestDto dto
    ) {
        if(senderId.equals(dto.receiverId())) {
            throw new BadRequestException("You cannot collaborate with yourself");
        }

        if(requestRepository.existsBySenderIdAndReceiverId(
                senderId, dto.receiverId()
        )) {
            throw new BadRequestException("Request already exists");
        }

        boolean alreadyConnected = connectionRepository.existsByUserOneIdAndUserTwoId(
                senderId,
                dto.receiverId()
        );

        if(alreadyConnected) {
            throw new BadRequestException("Already connected");
        }

        CollaborationRequest request = CollaborationRequest.builder()
                .senderId(senderId)
                .receiverId(dto.receiverId())
                .message(dto.message())
                .status(CollaborationStatus.PENDING)
                .build();

        request = requestRepository.save(request);

//        producer.publishRequestSent(
//                CollaborationRequestSentEvent
//                        .builder()
//                        .senderId(senderId)
//                        .receiverId(dto.receiverId())
//                        .message(dto.message())
//                        .build()
//        );
        return mapper.toResponse(request);
    }

    @Override
    public CollaborationRequestResponse acceptRequest(
            Long requestId,
            Long currentUserId
    ) {
        CollaborationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Request not found"
                        )
                );

        if(!request.getReceiverId().equals(currentUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }

        if(request.getStatus() != CollaborationStatus.PENDING) {
            throw new BadRequestException("Already processed");
        }

        request.setStatus(CollaborationStatus.ACCEPTED);
        requestRepository.save(request);

        CollaborationConnection connection =
                CollaborationConnection.builder()
                        .userOneId(request.getSenderId())
                        .userTwoId(request.getReceiverId())
                        .build();

        connectionRepository.save(connection);

//        producer.publishRequestAccepted(
//                CollaborationRequestAcceptedEvent.builder()
//                        .senderId(request.getSenderId())
//                        .receiverId(request.getReceiverId())
//                        .build()
//        );

        return mapper.toResponse(request);
    }

    @Override
    public CollaborationRequestResponse rejectRequest(
            Long requestId,
            Long currentUserId
    ) {
        CollaborationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Request not found"
                        )
                );

        if (request.getReceiverId() == null) {
            throw new BadRequestException(
                    "Collaboration request has no receiver assigned"
            );
        }

        if(!request.getReceiverId().equals(currentUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }

        if(request.getStatus() != CollaborationStatus.PENDING) {
            throw new BadRequestException("Already processed");
        }

        request.setStatus(CollaborationStatus.REJECTED);
        requestRepository.save(request);

//        NO CONNECTION

//        producer.publishRequestRejected(
//                CollaborationRequestRejectedEvent.builder()
//                        .senderId(request.getSenderId())
//                        .receiverId(request.getReceiverId())
//                        .build()
//        );

        return mapper.toResponse(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CollaborationRequestResponse> getSentRequests(
            Long userId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDirection);
        Page<CollaborationRequestResponse> requests = requestRepository.findBySenderId(
                userId, pageable
                )
                .map(mapper::toResponse);

        return new PagedResponse<>(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CollaborationRequestResponse> getReceivedRequests(
            Long userId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDirection);
        Page<CollaborationRequestResponse> requests = requestRepository.findByReceiverId(
                userId,
                pageable
        ).map(mapper::toResponse);

        return new PagedResponse<>(requests);
    }

    @Override
    public void cancelRequest(
            Long requestId,
            Long currentUserId
    ) {
        CollaborationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!Objects.equals(request.getSenderId(), currentUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }

        if (request.getStatus() != CollaborationStatus.PENDING) {
            throw new BadRequestException("Request already processed");
        }

        requestRepository.delete(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ConnectionResponse> getConnections(
            Long userId,
            int page,
            int size
    ) {
        Page<ConnectionResponse> connections = connectionRepository.findByUserOneId(
                userId,
                PageRequest.of(page, size)
        ).map(mapper::toResponse);

        return new PagedResponse<>(connections);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<UserProfileResponse>> searchResearchers(
            String keyword,
            Long id,
            String firstName,
            String lastName,
            String username,
            String email,
            String phoneNo,
            Role role,
            AccountStatus status,
            Boolean emailVerified,
            Boolean accountNonLocked,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,

            int page,
            int size,
            String sortBy
    ) {
        return authClient.searchUsers(
                keyword,
                id,
                firstName,
                lastName,
                username,
                email,
                phoneNo,
                role,
                status,
                emailVerified,
                accountNonLocked,
                createdAfter,
                createdBefore,
                page,
                size,
                sortBy
        );
    }

    @Override
    public ResearcherProfileResponse getResearcherProfile(
            Long currentUserId,
            Long researcherId
    ) {
        UserProfileResponse user = authClient.getUser(
                researcherId
        ).getData();

        List<ResearchPaperResponse> papers =
                researchClient.getPapersByAuthor(
                        researcherId
                );

        boolean connected = connectionRepository
                .existsByUserOneIdAndUserTwoId(
                        currentUserId,
                        researcherId
                ) || connectionRepository.existsByUserOneIdAndUserTwoId(
                researcherId,
                currentUserId
        );

        boolean pending = requestRepository.existsBySenderIdAndReceiverIdAndStatus(
                currentUserId,
                researcherId,
                CollaborationStatus.PENDING
        );

        return ResearcherProfileResponse
                .builder()
                .user(user)
                .papers(papers)
                .connected(connected)
                .pendingRequest(pending)
                .build();
    }
}
