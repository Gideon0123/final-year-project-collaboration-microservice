package com.example.COLLABORATION_SERVICE.feign;

import com.example.COLLABORATION_SERVICE.dto.ResearchPaperResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "RESEARCH-SERVICE")
public interface ResearchClient {

    @GetMapping("/research/papers/author/{authorId}")
    List<ResearchPaperResponse> getPapersByAuthor(
            @PathVariable Long authorId
    );

}
