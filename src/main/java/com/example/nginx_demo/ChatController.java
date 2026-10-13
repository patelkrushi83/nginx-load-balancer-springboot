package com.example.nginx_demo;

import com.example.nginx_demo.service.OllamaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class ChatController {

    private static final Logger logger =
            LoggerFactory.getLogger(ChatController.class);

    private final OllamaService ollamaService;
    private final String port;

    public ChatController(
            OllamaService ollamaService,
            @Value("${server.port:8080}") String port) {

        this.ollamaService = ollamaService;
        this.port = port;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @RequestBody ChatRequest request) {

        if (request == null ||
                request.prompt() == null ||
                request.prompt().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        logger.info("AI request received by backend on port {}", port);

        String answer = ollamaService.generateResponse(
                request.prompt()
        );

        logger.info("AI response completed by backend on port {}", port);

        return ResponseEntity.ok(
                new ChatResponse(answer, port)
        );
    }

    public record ChatRequest(String prompt) {}

    public record ChatResponse(String response, String backend) {}
}