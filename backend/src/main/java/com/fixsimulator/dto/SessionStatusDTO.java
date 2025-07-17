package com.fixsimulator.dto;

import lombok.Data;

@Data
public class SessionStatusDTO {
    private String sessionId;
    private SessionConfigDTO config;
    private boolean isRunning;
}