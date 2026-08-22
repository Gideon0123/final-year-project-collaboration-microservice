package com.example.COLLABORATION_SERVICE.mapper;

import com.example.COLLABORATION_SERVICE.dto.CollaborationRequestResponse;
import com.example.COLLABORATION_SERVICE.dto.ConnectionResponse;
import com.example.COLLABORATION_SERVICE.entity.CollaborationConnection;
import com.example.COLLABORATION_SERVICE.entity.CollaborationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollaborationMapper {

    CollaborationRequestResponse toResponse(
            CollaborationRequest request
    );

    ConnectionResponse toResponse(
            CollaborationConnection connection
    );

}