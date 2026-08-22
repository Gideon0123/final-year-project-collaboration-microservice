package com.example.COLLABORAION_SERVICE.mapper;

import com.example.COLLABORAION_SERVICE.dto.CollaborationRequestResponse;
import com.example.COLLABORAION_SERVICE.entity.CollaborationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollaborationMapper {

    CollaborationRequestResponse toResponse(CollaborationRequest request);
}