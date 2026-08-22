package com.example.COLLABORAION_SERVICE.service.impl;

import com.example.COLLABORAION_SERVICE.dto.CollaborationRequestResponse;
import com.example.COLLABORAION_SERVICE.dto.SendRequestDto;
import com.example.COLLABORAION_SERVICE.exception.BadRequestException;
import com.example.COLLABORAION_SERVICE.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CollaborationServiceImpl implements CollaborationService {



    @Transactional
    @Override
    public CollaborationRequestResponse sendRequest(
            Long senderId,
            SendRequestDto dto
    ) {
        if(senderId.equals(dto.receiverId())) {

            throw new BadRequestException(
                    "You cannot collaborate with yourself"
            );
        }
        return null;
    }

    @Override
    public CollaborationRequestResponse acceptRequest(Long requestId, Long currentUserId) {
        return null;
    }

    @Override
    public CollaborationRequestResponse rejectRequest(Long requestId, Long currentUserId) {
        return null;
    }
}
