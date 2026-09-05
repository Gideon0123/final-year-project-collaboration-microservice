//package com.example.COLLABORATION_SERVICE.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketTestController {
//
//    @MessageMapping("/test")
//    @SendTo("/queue/test")
//    public String test(String message) {
//
//        System.out.println(
//                "======================================"
//        );
//
//        System.out.println(
//                "STOMP TEST CONTROLLER CALLED"
//        );
//
//        System.out.println(
//                "Received message: " + message
//        );
//
//        System.out.println(
//                "======================================"
//        );
//
//        return "Server received: " + message;
//    }
//}