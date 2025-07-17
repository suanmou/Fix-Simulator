package com.fixsimulator;

import com.fixsimulator.dto.SessionConfigDTO;
import org.quickfixj.core.SessionID;
import org.quickfixj.core.SessionSettings;
import org.quickfixj.jmx.JmxExporter;
import org.quickfixj.netty.acceptor.NettyAcceptor;
import org.quickfixj.netty.initiator.NettyInitiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final FixApplication fixApplication;
    private final MessageFactory messageFactory;
    private final Map<String, SessionInstance> sessionInstances = new ConcurrentHashMap<>();

    @Autowired
    public SessionManager(FixApplication fixApplication, MessageFactory messageFactory) {
        this.fixApplication = fixApplication;
        this.messageFactory = messageFactory;
    }

    // 创建并启动新会话
    public String createAndStartSession(SessionConfigDTO config) throws Exception {
        String sessionKey = config.getSessionId() != null ? config.getSessionId() : generateSessionKey();

        // 如果会话已存在，先停止旧会话
        if (sessionInstances.containsKey(sessionKey)) {
            stopSession(sessionKey);
        }

        // 构建动态配置
        SessionSettings settings = createSessionSettings(config);
        SessionInstance instance = new SessionInstance();
        instance.setConfig(config);
        instance.setSettings(settings);

        // 根据连接类型创建会话
        if ("initiator".equalsIgnoreCase(config.getConnectionType())) {
            NettyInitiator initiator = new NettyInitiator(settings, fixApplication, messageFactory);
            initiator.start();
            instance.setInitiator(initiator);
        } else {
            NettyAcceptor acceptor = new NettyAcceptor(settings, fixApplication, messageFactory);
            acceptor.start();
            instance.setAcceptor(acceptor);
        }

        sessionInstances.put(sessionKey, instance);
        return sessionKey;
    }

    // 停止指定会话
    public void stopSession(String sessionKey) {
        SessionInstance instance = sessionInstances.get(sessionKey);
        if (instance != null) {
            try {
                if (instance.getInitiator() != null) {
                    instance.getInitiator().stop();
                } else if (instance.getAcceptor() != null) {
                    instance.getAcceptor().stop();
                }
            } catch (Exception e) {
                // 日志记录
            }
            sessionInstances.remove(sessionKey);
        }
    }

    // 停止所有会话
    @PreDestroy
    public void stopAllSessions() {
        sessionInstances.keySet().forEach(this::stopSession);
    }

    // 获取所有会话状态
    public Map<String, SessionStatusDTO> getSessionStatuses() {
        Map<String, SessionStatusDTO> statuses = new HashMap<>();
        sessionInstances.forEach((key, instance) -> {
            SessionStatusDTO status = new SessionStatusDTO();
            status.setSessionId(key);
            status.setConfig(instance.getConfig());
            status.setRunning(isSessionRunning(instance));
            statuses.put(key, status);
        });
        return statuses;
    }

    // 构建QuickFIX/J配置
    private SessionSettings createSessionSettings(SessionConfigDTO config) {
        SessionSettings settings = new SessionSettings();
        Map<String, String> configMap = config.toSettingsMap();

        // 添加默认设置
        SessionSettings defaultSettings = new SessionSettings();
        configMap.forEach((key, value) -> {
            try {
                defaultSettings.setString(key, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set session property: " + key, e);
            }
        });

        // 添加会话设置
        String sessionQualifier = config.getSessionId() != null ? config.getSessionId() : "DYNAMIC-" + UUID.randomUUID();
        settings.set(defaultSettings);
        settings.setString(sessionQualifier, "SessionQualifier", sessionQualifier);

        return settings;
    }

    private boolean isSessionRunning(SessionInstance instance) {
        if (instance.getInitiator() != null) {
            return instance.getInitiator().isRunning();
        } else if (instance.getAcceptor() != null) {
            return instance.getAcceptor().isRunning();
        }
        return false;
    }

    private String generateSessionKey() {
        return "FIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // 内部类用于管理会话实例
    private static class SessionInstance {
        private SessionConfigDTO config;
        private SessionSettings settings;
        private NettyInitiator initiator;
        private NettyAcceptor acceptor;

        // Getters and setters
        public SessionConfigDTO getConfig() { return config; }
        public void setConfig(SessionConfigDTO config) { this.config = config; }
        public SessionSettings getSettings() { return settings; }
        public void setSettings(SessionSettings settings) { this.settings = settings; }
        public NettyInitiator getInitiator() { return initiator; }
        public void setInitiator(NettyInitiator initiator) { this.initiator = initiator; }
        public NettyAcceptor getAcceptor() { return acceptor; }
        public void setAcceptor(NettyAcceptor acceptor) { this.acceptor = acceptor; }
    }
}