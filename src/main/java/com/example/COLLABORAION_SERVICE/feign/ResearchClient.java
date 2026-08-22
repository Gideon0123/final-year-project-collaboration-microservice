package com.example.COLLABORAION_SERVICE.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "RESEARCH-SERVICE")
public interface ResearchClient {

}
