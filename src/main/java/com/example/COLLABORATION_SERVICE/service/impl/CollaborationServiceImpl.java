package com.example.COLLABORATION_SERVICE.service.impl;

import com.example.COLLABORATION_SERVICE.dto.CollaborationRequestResponse;
import com.example.COLLABORATION_SERVICE.dto.SendRequestDto;
import com.example.COLLABORATION_SERVICE.dto.event.CollaborationRequestAcceptedEvent;
import com.example.COLLABORATION_SERVICE.dto.event.CollaborationRequestRejectedEvent;
import com.example.COLLABORATION_SERVICE.entity.CollaborationConnection;
import com.example.COLLABORATION_SERVICE.entity.CollaborationRequest;
import com.example.COLLABORATION_SERVICE.enums.CollaborationStatus;
import com.example.COLLABORATION_SERVICE.exception.AccessDeniedException;
import com.example.COLLABORATION_SERVICE.exception.BadRequestException;
import com.example.COLLABORATION_SERVICE.exception.ResourceNotFoundException;
import com.example.COLLABORATION_SERVICE.mapper.CollaborationMapper;
import com.example.COLLABORATION_SERVICE.publisher.CollaborationProducer;
import com.example.COLLABORATION_SERVICE.repository.CollaborationConnectionRepository;
import com.example.COLLABORATION_SERVICE.repository.CollaborationRequestRepository;
import com.example.COLLABORATION_SERVICE.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationServiceImpl implements CollaborationService {

    private final CollaborationRequestRepository requestRepository;
    private final CollaborationConnectionRepository connectionRepository;
    private final CollaborationProducer producer;
    private final CollaborationMapper mapper;


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

        producer.publishRequestAccepted(
                CollaborationRequestAcceptedEvent.builder()
                        .senderId(request.getSenderId())
                        .receiverId(request.getReceiverId())
                        .build()
        );

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

        if(!request.getReceiverId().equals(currentUserId)) {
            throw new AccessDeniedException("Unauthorized");
        }

        if(request.getStatus() != CollaborationStatus.PENDING) {
            throw new BadRequestException("Already processed");
        }

        request.setStatus(CollaborationStatus.REJECTED);
        requestRepository.save(request);

//        NO CONNECTION

        producer.publishRequestRejected(
                CollaborationRequestRejectedEvent.builder()
                        .senderId(request.getSenderId())
                        .receiverId(request.getReceiverId())
                        .build()
        );

        return mapper.toResponse(request);
    }
}
