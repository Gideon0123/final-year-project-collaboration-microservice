package com.example.COLLABORATION_SERVICE.controller;

import com.example.COLLABORATION_SERVICE.service.CollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/collaboration")
@RequiredArgsConstructor
public class InternalCollaborationController {

    private final CollaborationService collaborationService;

    @GetMapping("/connections/check")
    public boolean areConnected(
            @RequestParam Long userId,
            @RequestParam Long otherUserId
    ) {

        return collaborationService.areConnected(
                userId,
                otherUserId
        );
    }
}