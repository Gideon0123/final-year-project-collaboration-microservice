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
import com.example.COLLABORATION_SERVICE.utils.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
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
    @Caching(
            evict = {
                    @CacheEvict(
                            value = CacheNames.COLLABORATION_REQUESTS,
                            allEntries = true
                    ),
                    @CacheEvict(
                            value = CacheNames.RESEARCHER_PROFILES,
                            allEntries = true
                    )
            }
    )
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

        boolean alreadyConnected = connectionRepository.connectionExists(
                senderId,
                dto.receiverId()
        );
//                ||
//                connectionRepository.existsByUserOneIdAndUserTwoId(
//                        dto.receiverId(),
//                        senderId
//                );

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
    @Caching(
            evict = {
                    @CacheEvict(
                            value = CacheNames.COLLABORATION_REQUESTS,
                            allEntries = true
                    ),
                    @CacheEvict(
                            value = CacheNames.COLLABORATION_CONNECTIONS,
                            allEntries = true
                    ),
                    @CacheEvict(
                            value = CacheNames.RESEARCHER_PROFILES,
                            allEntries = true
                    )
            }
    )
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
    @Caching(
            evict = {
                    @CacheEvict(
                            value = CacheNames.COLLABORATION_REQUESTS,
                            allEntries = true
                    ),
                    @CacheEvict(
                            value = CacheNames.RESEARCHER_PROFILES,
                            allEntries = true
                    )
            }
    )
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
    @Cacheable(
            value = CacheNames.COLLABORATION_REQUESTS,
            key = "T(com.example.COLLABORATION_SERVICE.utils.CacheKeys)"
                    + ".sentRequests(#userId, #page, #size, #sortBy, #sortDirection)"
    )
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
    @Cacheable(
            value = CacheNames.COLLABORATION_REQUESTS,
            key = "T(com.example.COLLABORATION_SERVICE.utils.CacheKeys)"
                    + ".receivedRequests(#userId, #page, #size, #sortBy, #sortDirection)"
    )
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
    @Caching(
            evict = {
                    @CacheEvict(
                            value = CacheNames.COLLABORATION_REQUESTS,
                            allEntries = true
                    ),
                    @CacheEvict(
                            value = CacheNames.RESEARCHER_PROFILES,
                            allEntries = true
                    )
            }
    )
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
    @Cacheable(
            value = CacheNames.COLLABORATION_CONNECTIONS,
            key = "T(com.example.COLLABORATION_SERVICE.utils.CacheKeys)"
                    + ".connections(#userId, #page, #size)"
    )
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
    @Cacheable(
            value = CacheNames.RESEARCHER_SEARCH,
            key = "T(com.example.COLLABORATION_SERVICE.utils.CacheKeys)"
                    + ".researcherSearch("
                    + "#keyword, "
                    + "#id, "
                    + "#firstName, "
                    + "#lastName, "
                    + "#username, "
                    + "#email, "
                    + "#phoneNo, "
                    + "#role, "
                    + "#status, "
                    + "#emailVerified, "
                    + "#accountNonLocked, "
                    + "#createdAfter, "
                    + "#createdBefore, "
                    + "#page, "
                    + "#size, "
                    + "#sortBy"
                    + ")"
    )
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

    @Transactional(readOnly = true)
    @Override
    @Cacheable(
            value = CacheNames.RESEARCHER_PROFILES,
            key = "T(com.example.COLLABORATION_SERVICE.utils.CacheKeys)"
                    + ".researcherProfile("
                    + "#currentUserId, "
                    + "#researcherId, "
                    + "#page, "
                    + "#size, "
                    + "#sortBy"
                    + ")"
    )
    public ResearcherProfileResponse getResearcherProfile(
            Long currentUserId,
            Long researcherId,
            int page,
            int size,
            String sortBy
    ) {
        UserProfileResponse user = authClient.getUser(
                researcherId
        ).getData();

        ApiResponse<PagedResponse<ResearchPaperResponse>> papers = researchClient.getPapersByAuthor(
                researcherId, page, size, sortBy, "desc"
        );

        boolean connected = connectionRepository
                .connectionExists(
                        currentUserId,
                        researcherId
                );
//                || connectionRepository.existsByUserOneIdAndUserTwoId(
//                researcherId,
//                currentUserId
//        );

        boolean pending = requestRepository.existsBySenderIdAndReceiverIdAndStatus(
                currentUserId,
                researcherId,
                CollaborationStatus.PENDING
        ) || requestRepository.existsBySenderIdAndReceiverIdAndStatus(
                researcherId,
                currentUserId,
                CollaborationStatus.PENDING
        );

        return ResearcherProfileResponse
                .builder()
                .user(user)
                .papers(papers.getData())
                .connected(connected)
                .pendingRequest(pending)
                .build();
    }
}