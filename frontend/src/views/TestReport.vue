<template>
  <el-container>
    <el-header>测试报告</el-header>
    <el-main>
      <el-card v-if="report">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-descriptions title="测试概要">
              <el-descriptions-item label="场景名称">{{ report.scenarioName }}</el-descriptions-item>
              <el-descriptions-item label="开始时间">{{ formatDate(report.startTime) }}</el-descriptions-item>
              <el-descriptions-item label="结束时间">{{ formatDate(report.endTime) }}</el-descriptions-item>
              <el-descriptions-item label="总耗时">{{ report.duration }}ms</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="report.status === 'SUCCESS' ? 'success' : 'danger'">{{ report.status }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="步骤总数">{{ report.totalSteps }}</el-descriptions-item>
              <el-descriptions-item label="成功步骤">{{ report.successSteps }}</el-descriptions-item>
              <el-descriptions-item label="失败步骤">{{ report.failedSteps }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <div style="text-align: right;">
              <el-button type="primary" @click="exportReport">导出报告</el-button>
              <el-button @click="runRegression">添加到回归测试</el-button>
            </div>
          </el-col>
        </el-row>
        
        <el-divider>详细步骤</el-divider>
        <el-table :data="report.stepResults" border style="width: 100%">
          <el-table-column prop="stepOrder" label="步骤" width="80"></el-table-column>
          <el-table-column prop="actionType" label="操作类型" width="120"></el-table-column>
          <el-table-column prop="messageType" label="消息类型" width="150"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : 'danger'">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="耗时(ms)" width="100"></el-table-column>
          <el-table-column label="详情" width="150">
            <template slot-scope="scope">
              <el-button size="small" @click="showStepDetails(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'TestReport',
  data() {
    return {
      report: null,
      stepDetailsVisible: false,
      currentStepResult: null
    }
  },
  mounted() {
    const reportId = this.$route.params.reportId;
    this.loadReport(reportId);
  },
  methods: {
    loadReport(reportId) {
      this.$axios.get(`/api/test-reports/${reportId}`)
        .then(response => {
          this.report = response.data;
        })
        .catch(error => {
          this.$message.error('加载报告失败: ' + error.message);
        });
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString();
    },
    exportReport() {
      window.open(`/api/test-reports/${this.report.id}/export`);
    },
    showStepDetails(stepResult) {
      this.currentStepResult = stepResult;
      this.stepDetailsVisible = true;
    },
    runRegression() {
      this.$axios.post('/api/regression-tests', {
        scenarioIds: [this.report.scenarioId]
      }).then(response => {
        this.$message.success('已添加到回归测试');
        this.$router.push({name: 'RegressionTest'});
      }).catch(error => {
        this.$message.error('添加失败: ' + error.message);
      });
    }
  }
}
</script>