<template>
  <div class="message-monitor-container">
    <el-card title="消息监控">
      <div class="filter-bar">
        <el-select v-model="selectedSessionId" placeholder="选择会话">
          <el-option label="所有会话" value=""></el-option>
          <el-option
            v-for="session in $store.state.activeSessions"
            :key="session.sessionId"
            :label="session.sessionId"
            :value="session.sessionId"
          ></el-option>
        </el-select>

        <el-select v-model="messageDirection" placeholder="消息方向">
          <el-option label="所有方向" value=""></el-option>
          <el-option label="发送" value="OUTGOING"></el-option>
          <el-option label="接收" value="INCOMING"></el-option>
        </el-select>

        <el-button type="primary" @click="clearMessages">清空消息</el-button>
      </div>

      <el-table
        :data="filteredMessages"
        style="width: 100%"
        height="600px"
        border
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="timestamp" label="时间" width="180"></el-table-column>
        <el-table-column prop="sessionId" label="会话ID" width="150"></el-table-column>
        <el-table-column prop="direction" label="方向" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.direction === 'INCOMING' ? 'info' : 'success'">
              {{ scope.row.direction === 'INCOMING' ? '接收' : '发送' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="messageType" label="消息类型" width="180"></el-table-column>
        <el-table-column prop="message" label="消息内容">
          <template slot-scope="scope">
            <el-popover
              placement="top"
              width="600"
              trigger="click"
              content="<pre>{{ formatMessage(scope.row.message) }}</pre>"
              v-html="true"
            >
              <span slot="reference">{{ truncateMessage(scope.row.message) }}</span>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'MessageMonitor',
  data() {
    return {
      selectedSessionId: '',
      messageDirection: ''
    };
  },
  computed: {
    filteredMessages() {
      return this.$store.getters.filteredMessages(this.selectedSessionId).filter(msg => {
        if (this.messageDirection) {
          return msg.direction === this.messageDirection;
        }
        return true;
      });
    }
  },
  mounted() {
    // 监听WebSocket消息
    this.$options.sockets.onmessage = (event) => {
      const message = JSON.parse(event.data);
      this.$store.commit('addMessage', message);
    };
  },
  methods: {
    tableRowClassName({ row }) {
      return row.direction === 'INCOMING' ? 'incoming-row' : 'outgoing-row';
    },
    truncateMessage(message) {
      if (message.length > 50) {
        return message.substring(0, 50) + '...';
      }
      return message;
    },
    formatMessage(message) {
      // 格式化FIX消息以便于阅读
      return message.replace(/\|/g, '|\n');
    },
    clearMessages() {
      this.$store.state.messages = [];
    }
  }
};
</script>

<style scoped>
.message-monitor-container {
  padding: 20px;
}
.filter-bar {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
  align-items: center;
}
.incoming-row {
  background-color: #f0f7ff;
}
.outgoing-row {
  background-color: #f6ffed;
}
</style>