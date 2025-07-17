package com.fixsimulator.model;

import javax.persistence.*;

@Entity
@Table(name = "test_steps")
public class TestStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int stepOrder;
    private String actionType; // 添加 NETWORK_INTERRUPT 和 NETWORK_RESTORE 选项
    private String messageType;
    private String messageContent;
    private long delayMillis; // For DELAY type
    private String assertionExpression; // For ASSERT type
    private int timeoutSeconds; // For RECEIVE type
    private long networkInterruptDuration; // 新增：网络中断持续时间(毫秒)
    
    // Getters and setters
}