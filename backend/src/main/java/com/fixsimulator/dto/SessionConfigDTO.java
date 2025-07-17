package com.fixsimulator.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class SessionConfigDTO {
    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Connection type is required")
    private String connectionType; // initiator/acceptor

    @NotBlank(message = "SenderCompID is required")
    private String senderCompID;

    @NotBlank(message = "TargetCompID is required")
    private String targetCompID;

    @NotBlank(message = "BeginString is required")
    private String beginString = "FIX.4.4";

    @NotNull(message = "Heartbeat interval is required")
    private Integer heartBtInt = 30;

    // For initiator
    private String socketConnectHost;
    private Integer socketConnectPort;

    // For acceptor
    private Integer socketAcceptPort;

    // Advanced settings
    private Boolean useDataDictionary = true;
    private String dataDictionary = "FIX44.xml";
    private Integer reconnectInterval = 60;

    // Convert DTO to QuickFIX/J SessionSettings map
    public Map<String, String> toSettingsMap() {
        Map<String, String> settings = new HashMap<>();
        settings.put("ConnectionType", connectionType);
        settings.put("SenderCompID", senderCompID);
        settings.put("TargetCompID", targetCompID);
        settings.put("BeginString", beginString);
        settings.put("HeartBtInt", heartBtInt.toString());
        settings.put("ReconnectInterval", reconnectInterval.toString());
        settings.put("UseDataDictionary", useDataDictionary ? "Y" : "N");
        settings.put("DataDictionary", dataDictionary);

        if ("initiator".equalsIgnoreCase(connectionType)) {
            settings.put("SocketConnectHost", socketConnectHost);
            settings.put("SocketConnectPort", socketConnectPort.toString());
        } else {
            settings.put("SocketAcceptPort", socketAcceptPort.toString());
        }

        return settings;
    }
}