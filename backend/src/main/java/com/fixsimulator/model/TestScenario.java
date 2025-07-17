package com.fixsimulator.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "test_scenarios")
public class TestScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "scenario_id")
    private List<TestStep> steps;
    
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    
    // Getters and setters
}