package com.fixsimulator.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class NetworkControlService {
    private static final String CONTAINER_NAME = "fix-simulator-backend";
    
    public void interruptNetwork(long durationMillis) throws IOException, InterruptedException {
        // 断开网络连接
        executeCommand("docker network disconnect bridge " + CONTAINER_NAME);
        
        // 等待指定时长
        Thread.sleep(durationMillis);
        
        // 恢复网络连接
        executeCommand("docker network connect bridge " + CONTAINER_NAME);
    }
    
    private void executeCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command execution failed: " + command);
        }
    }
}