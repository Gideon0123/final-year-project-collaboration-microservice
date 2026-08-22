package com.example.COLLABORATION_SERVICE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

        Long id;
        String firstName;
        String lastName;
        String username;
        String email;
        String institution;
        String faculty;
        String department;
        String role;
}