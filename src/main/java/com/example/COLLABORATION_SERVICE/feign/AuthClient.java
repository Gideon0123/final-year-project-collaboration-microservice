package com.example.COLLABORATION_SERVICE.feign;

import com.example.COLLABORATION_SERVICE.dto.ApiResponse;
import com.example.COLLABORATION_SERVICE.dto.UserProfileResponse;
import com.example.COLLABORATION_SERVICE.enums.AccountStatus;
import com.example.COLLABORATION_SERVICE.enums.Role;
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    @GetMapping("/admin/users/search")
    ApiResponse<PagedResponse<UserProfileResponse>> searchUsers(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNo,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) Boolean emailVerified,
            @RequestParam(required = false) Boolean accountNonLocked,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdBefore,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    );

    @GetMapping("/users/{id}")
    ApiResponse<UserProfileResponse> getUser(
            @PathVariable Long id
    );

}