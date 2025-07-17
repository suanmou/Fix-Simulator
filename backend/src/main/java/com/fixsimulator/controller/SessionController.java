package com.fixsimulator.controller;

import com.fixsimulator.SessionManager;
import com.fixsimulator.dto.SessionConfigDTO;
import com.fixsimulator.dto.SessionStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionManager sessionManager;

    @Autowired
    public SessionController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // 创建新会话
    @PostMapping
    public ResponseEntity<String> createSession(@Valid @RequestBody SessionConfigDTO config) {
        try {
            String sessionId = sessionManager.createAndStartSession(config);
            return ResponseEntity.ok(sessionId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create session: " + e.getMessage());
        }
    }

    // 停止会话
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> stopSession(@PathVariable String sessionId) {
        sessionManager.stopSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    // 获取所有会话状态
    @GetMapping
    public ResponseEntity<Map<String, SessionStatusDTO>> getSessionStatuses() {
        return ResponseEntity.ok(sessionManager.getSessionStatuses());
    }
}