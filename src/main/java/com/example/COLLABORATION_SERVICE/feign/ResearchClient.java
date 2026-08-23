package com.example.COLLABORATION_SERVICE.feign;

import com.example.COLLABORATION_SERVICE.dto.ApiResponse;
import com.example.COLLABORATION_SERVICE.dto.ResearchPaperResponse;
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RESEARCH-SERVICE")
public interface ResearchClient {

    @GetMapping("/research/papers/author/{authorId}")
    ApiResponse<PagedResponse<ResearchPaperResponse>> getPapersByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    );

}
