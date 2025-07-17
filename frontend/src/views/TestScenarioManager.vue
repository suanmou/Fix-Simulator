<template>
  <el-container>
    <el-header>测试场景管理</el-header>
    <el-main>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <el-input placeholder="场景名称" v-model="newScenarioName" style="margin-bottom: 10px;"></el-input>
            <el-button type="primary" @click="createScenario">创建场景</el-button>
            
            <el-divider>场景列表</el-divider>
            <el-tree
              :data="scenarios"
              :props="{label: 'name'}"
              @node-click="selectScenario"
              style="max-height: 500px; overflow-y: auto;">
            </el-tree>
          </el-card>
        </el-col>
        
        <el-col :span="18">
          <el-card v-if="selectedScenario">
            <el-form :model="selectedScenario" label-width="100px">
              <el-form-item label="场景名称">
                <el-input v-model="selectedScenario.name"></el-input>
              </el-form-item>
              <el-form-item label="场景描述">
                <el-input type="textarea" v-model="selectedScenario.description"></el-input>
              </el-form-item>
            </el-form>
            
            <el-divider>测试步骤</el-divider>
            <el-table :data="selectedScenario.steps" border style="width: 100%">
              <el-table-column prop="stepOrder" label="步骤" width="80"></el-table-column>
              <el-table-column prop="actionType" label="操作类型" width="120">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.actionType" @change="updateStep(scope.row)">
                    <el-option label="发送消息" value="SEND"></el-option>
                    <el-option label="接收消息" value="RECEIVE"></el-option>
                    <el-option label="延迟等待" value="DELAY"></el-option>
                    <el-option label="断言验证" value="ASSERT"></el-option>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="messageType" label="消息类型" width="150"></el-table-column>
              <el-table-column label="操作" width="120">
                <template slot-scope="scope">
                  <el-button size="small" @click="editStep(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteStep(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div style="margin-top: 10px;">
              <el-button type="primary" @click="addStep">添加步骤</el-button>
              <el-button @click="saveScenario">保存场景</el-button>
              <el-button type="success" @click="executeScenario">执行场景</el-button>
              <el-button type="warning" @click="replayScenario">回放场景</el-button>
            </div>
          </el-card>
          
          <el-card v-else>
            <div class="empty-state">请从左侧选择或创建测试场景</div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'TestScenarioManager',
  data() {
    return {
      newScenarioName: '',
      scenarios: [],
      selectedScenario: null,
      stepDialogVisible: false,
      currentStep: null
    }
  },
  mounted() {
    this.loadScenarios();
  },
  methods: {
    loadScenarios() {
      // 从后端加载场景列表
      this.$axios.get('/api/test-scenarios')
        .then(response => {
          this.scenarios = response.data;
        })
        .catch(error => {
          this.$message.error('加载场景失败: ' + error.message);
        });
    },
    createScenario() {
      if (!this.newScenarioName.trim()) {
        this.$message.warning('请输入场景名称');
        return;
      }
      
      const newScenario = {
        name: this.newScenarioName,
        description: '',
        steps: []
      };
      
      this.$axios.post('/api/test-scenarios', newScenario)
        .then(response => {
          this.$message.success('场景创建成功');
          this.newScenarioName = '';
          this.loadScenarios();
        })
        .catch(error => {
          this.$message.error('创建场景失败: ' + error.message);
        });
    },
    selectScenario(scenario) {
      this.selectedScenario = {...scenario};
    },
    saveScenario() {
      if (!this.selectedScenario) return;
      
      this.$axios.put(`/api/test-scenarios/${this.selectedScenario.id}`, this.selectedScenario)
        .then(response => {
          this.$message.success('场景保存成功');
          this.loadScenarios();
        })
        .catch(error => {
          this.$message.error('保存场景失败: ' + error.message);
        });
    },
    addStep() {
      if (!this.selectedScenario) {
        this.$message.warning('请先选择场景');
        return;
      }
      
      const newStep = {
        stepOrder: this.selectedScenario.steps.length + 1,
        actionType: 'SEND',
        messageType: '',
        messageContent: '',
        delayMillis: 1000,
        assertionExpression: ''
      };
      
      this.currentStep = newStep;
      this.stepDialogVisible = true;
    },
    editStep(step) {
      this.currentStep = {...step};
      this.stepDialogVisible = true;
    },
    deleteStep(step) {
      this.selectedScenario.steps = this.selectedScenario.steps.filter(s => s.id !== step.id);
      // 重新排序步骤
      this.selectedScenario.steps.forEach((s, index) => s.stepOrder = index + 1);
    },
    updateStep(step) {
      // 步骤变更时更新
    },
    executeScenario() {
      this.$axios.post(`/api/test-scenarios/${this.selectedScenario.id}/execute`)
        .then(response => {
          this.$message.success('场景执行成功');
          this.$router.push({name: 'TestReport', params: {reportId: response.data.id}});
        })
        .catch(error => {
          this.$message.error('执行场景失败: ' + error.message);
        });
    },
    replayScenario() {
      this.$axios.post(`/api/test-scenarios/${this.selectedScenario.id}/replay`)
        .then(response => {
          this.$message.success('场景回放成功');
          this.$router.push({name: 'TestReport', params: {reportId: response.data.id}});
        })
        .catch(error => {
          this.$message.error('回放场景失败: ' + error.message);
        });
    }
  }
}
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 50px 0;
  color: #909399;
  font-size: 14px;
}
</style>