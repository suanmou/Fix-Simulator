<template>
  <el-container>
    <el-header>自动化回归测试</el-header>
    <el-main>
      <el-card>
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="测试套件">
            <el-select v-model="searchForm.suiteId" placeholder="选择测试套件">
              <el-option v-for="suite in testSuites" :key="suite.id" :label="suite.name" :value="suite.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadRegressionReports">查询</el-button>
            <el-button @click="createTestSuite">创建测试套件</el-button>
          </el-form-item>
        </el-form>

        <el-divider>测试套件场景</el-divider>
        <el-transfer
          v-model="selectedScenarioIds"
          :data="availableScenarios"
          :titles="['可用场景', '已选场景']"
          @change="handleScenarioChange">
        </el-transfer>

        <div style="text-align: center; margin: 20px 0;">
          <el-button type="success" size="large" @click="runRegressionTest">执行回归测试</el-button>
        </div>

        <el-divider>回归测试历史</el-divider>
        <el-table :data="regressionReports" border style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="suiteName" label="测试套件" width="180"></el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
          <el-table-column prop="duration" label="总耗时" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : 'danger'">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalScenarios" label="场景总数" width="100"></el-table-column>
          <el-table-column prop="successScenarios" label="成功场景" width="100"></el-table-column>
          <el-table-column prop="failedScenarios" label="失败场景" width="100"></el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button size="small" @click="viewRegressionReport(scope.row.id)">查看报告</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'RegressionTest',
  data() {
    return {
      searchForm: {
        suiteId: ''
      },
      testSuites: [],
      availableScenarios: [],
      selectedScenarioIds: [],
      regressionReports: []
    }
  },
  mounted() {
    this.loadTestSuites();
    this.loadAvailableScenarios();
    this.loadRegressionReports();
  },
  methods: {
    loadTestSuites() {
      this.$axios.get('/api/regression-suites')
        .then(response => {
          this.testSuites = response.data;
        });
    },
    loadAvailableScenarios() {
      this.$axios.get('/api/test-scenarios')
        .then(response => {
          this.availableScenarios = response.data.map(scenario => ({
            key: scenario.id,
            label: scenario.name
          }));
        });
    },
    loadRegressionReports() {
      const url = this.searchForm.suiteId ? 
        `/api/regression-reports?suiteId=${this.searchForm.suiteId}` : 
        '/api/regression-reports';

      this.$axios.get(url)
        .then(response => {
          this.regressionReports = response.data;
        });
    },
    createTestSuite() {
      this.$prompt('请输入测试套件名称', '创建测试套件', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        this.$axios.post('/api/regression-suites', {
          name: value,
          scenarioIds: this.selectedScenarioIds
        }).then(response => {
          this.$message.success('测试套件创建成功');
          this.loadTestSuites();
        });
      });
    },
    handleScenarioChange() {
      // 当选择的场景变化时触发
    },
    runRegressionTest() {
      if (!this.searchForm.suiteId) {
        this.$message.warning('请先选择测试套件');
        return;
      }

      this.$axios.post(`/api/regression-suites/${this.searchForm.suiteId}/execute`)
        .then(response => {
          this.$message.success('回归测试已开始执行');
          setTimeout(() => {
            this.loadRegressionReports();
          }, 2000);
        });
    },
    viewRegressionReport(reportId) {
      this.$router.push({name: 'RegressionReport', params: {reportId}});
    }
  }
}
</script>