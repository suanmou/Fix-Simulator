package com.fixsimulator.service;

import com.fixsimulator.model.TestScenario;
import com.fixsimulator.model.TestStep;
import com.fixsimulator.model.TestReport;
import java.util.List;

public interface TestScenarioService {
    // 场景管理
    TestScenario createScenario(String name, String description, List<TestStep> steps);
    TestScenario saveScenario(Long scenarioId, TestScenario scenario);
    TestScenario getScenario(Long scenarioId);
    List<TestScenario> listScenarios();
    void deleteScenario(Long scenarioId);
    
    // 场景执行
    TestReport executeScenario(Long scenarioId);
    TestReport replayScenario(Long scenarioId, int speedFactor);
    
    // 自动化回归测试
    List<TestReport> runRegressionTestSuite(List<Long> scenarioIds);
    TestReport generateRegressionReport(List<TestReport> testReports);
}