<template>
  <div class="session-config-container">
    <el-card title="FIX会话配置">
      <el-form ref="configForm" :model="config" :rules="rules" label-width="150px">
        <el-form-item label="会话ID (可选)">
          <el-input v-model="config.sessionId" placeholder="留空将自动生成"></el-input>
        </el-form-item>

        <el-form-item label="连接类型" prop="connectionType">
          <el-radio-group v-model="config.connectionType" @change="handleConnectionTypeChange">
            <el-radio label="initiator">发起方 (Initiator)</el-radio>
            <el-radio label="acceptor">接收方 (Acceptor)</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="发送方ID (SenderCompID)" prop="senderCompID">
          <el-input v-model="config.senderCompID"></el-input>
        </el-form-item>

        <el-form-item label="目标方ID (TargetCompID)" prop="targetCompID">
          <el-input v-model="config.targetCompID"></el-input>
        </el-form-item>

        <el-form-item label="协议版本" prop="beginString">
          <el-select v-model="config.beginString">
            <el-option label="FIX.4.4" value="FIX.4.4"></el-option>
            <el-option label="FIX.4.3" value="FIX.4.3"></el-option>
            <el-option label="FIX.4.2" value="FIX.4.2"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="心跳间隔 (秒)" prop="heartBtInt">
          <el-input type="number" v-model.number="config.heartBtInt"></el-input>
        </el-form-item>

        <template v-if="config.connectionType === 'initiator'">
          <el-form-item label="连接主机" prop="socketConnectHost">
            <el-input v-model="config.socketConnectHost"></el-input>
          </el-form-item>
          <el-form-item label="连接端口" prop="socketConnectPort">
            <el-input type="number" v-model.number="config.socketConnectPort"></el-input>
          </el-form-item>
        </template>

        <template v-if="config.connectionType === 'acceptor'">
          <el-form-item label="监听端口" prop="socketAcceptPort">
            <el-input type="number" v-model.number="config.socketAcceptPort"></el-input>
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" @click="submitForm">创建会话</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card title="活动会话" style="margin-top: 20px;">
      <el-table :data="sessionList">
        <el-table-column prop="sessionId" label="会话ID"></el-table-column>
        <el-table-column prop="connectionType" label="连接类型"></el-table-column>
        <el-table-column prop="senderCompID" label="发送方ID"></el-table-column>
        <el-table-column prop="targetCompID" label="目标方ID"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isRunning ? 'success' : 'danger'">{{ scope.row.isRunning ? '运行中' : '已停止' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button @click="stopSession(scope.row.sessionId)" type="danger" size="small">停止</el-button>
            <el-button @click="selectSession(scope.row.sessionId)" type="primary" size="small">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'SessionConfig',
  data() {
    return {
      config: {
        sessionId: '',
        connectionType: 'initiator',
        senderCompID: '',
        targetCompID: '',
        beginString: 'FIX.4.4',
        heartBtInt: 30,
        socketConnectHost: 'localhost',
        socketConnectPort: 9876,
        socketAcceptPort: 9876
      },
      rules: {
        connectionType: [{ required: true, message: '请选择连接类型', trigger: 'change' }],
        senderCompID: [{ required: true, message: '请输入发送方ID', trigger: 'blur' }],
        targetCompID: [{ required: true, message: '请输入目标方ID', trigger: 'blur' }],
        heartBtInt: [{ required: true, message: '请输入心跳间隔', trigger: 'blur' }],
        socketConnectHost: [{
          required: () => this.config.connectionType === 'initiator',
          message: '请输入连接主机',
          trigger: 'blur'
        }],
        socketConnectPort: [{
          required: () => this.config.connectionType === 'initiator',
          message: '请输入连接端口',
          trigger: 'blur'
        }],
        socketAcceptPort: [{
          required: () => this.config.connectionType === 'acceptor',
          message: '请输入监听端口',
          trigger: 'blur'
        }]
      }
    };
  },
  computed: {
    sessionList() {
      return Object.values(this.$store.state.activeSessions).map(session => ({
        sessionId: session.sessionId,
        connectionType: session.config.connectionType,
        senderCompID: session.config.senderCompID,
        targetCompID: session.config.targetCompID,
        isRunning: session.isRunning
      }));
    }
  },
  mounted() {
    this.fetchSessions();
    // 定期刷新会话状态
    this.interval = setInterval(() => this.fetchSessions(), 3000);
  },
  beforeDestroy() {
    clearInterval(this.interval);
  },
  methods: {
    fetchSessions() {
      this.$store.dispatch('fetchSessions');
    },
    handleConnectionTypeChange() {
      // 重置端口号
      if (this.config.connectionType === 'initiator') {
        this.config.socketConnectPort = 9876;
      } else {
        this.config.socketAcceptPort = 9876;
      }
    },
    submitForm() {
      this.$refs.configForm.validate(valid => {
        if (valid) {
          this.$store.dispatch('createSession', this.config).then(() => {
            this.$message.success('会话创建成功');
            this.resetForm();
          }).catch(error => {
            this.$message.error(`创建失败: ${error.response.data}`);
          });
        }
      });
    },
    resetForm() {
      this.$refs.configForm.resetFields();
    },
    stopSession(sessionId) {
      this.$confirm('确定要停止此会话吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('stopSession', sessionId).then(() => {
          this.$message.success('会话已停止');
        });
      });
    },
    selectSession(sessionId) {
      this.$store.commit('setCurrentSessionId', sessionId);
      this.$message.success(`已选择会话: ${sessionId}`);
      // 跳转到消息编辑器
      this.$router.push('/message-editor');
    }
  }
};
</script>

<style scoped>
.session-config-container {
  padding: 20px;
}
</style>