package com.example.COLLABORATION_SERVICE.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

}