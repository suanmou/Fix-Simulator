<template>
  <div class="message-editor-container">
    <el-card v-if="!currentSessionId" title="提示">
      <p>请先在会话配置页面选择一个活动会话</p>
      <el-button type="primary" @click="goToSessionConfig">前往会话配置</el-button>
    </el-card>

    <el-card v-else title="FIX消息编辑器">
      <el-form ref="messageForm" :model="message" label-width="150px">
        <el-form-item label="消息类型" prop="messageType">
          <el-select v-model="message.messageType" @change="handleMessageTypeChange">
            <el-option
              v-for="type in $store.state.fixMessageTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <div v-for="(field, index) in messageFields" :key="index" class="message-field">
          <el-form-item :label="field.name" :prop="field.tag">
            <el-input v-model="message.fields[field.tag]"></el-input>
          </el-form-item>
        </div>

        <el-form-item>
          <el-button type="primary" @click="sendMessage">发送消息</el-button>
          <el-button @click="resetMessageForm">重置</el-button>
          <el-button @click="addCustomField">添加自定义字段</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'MessageEditor',
  data() {
    return {
      message: {
        messageType: '',
        fields: {}
      },
      messageFields: []
    };
  },
  computed: {
    currentSessionId() {
      return this.$store.state.currentSessionId;
    }
  },
  methods: {
    goToSessionConfig() {
      this.$router.push('/session-config');
    },
    handleMessageTypeChange(type) {
      // 根据消息类型动态生成字段
      this.messageFields = this.generateMessageFields(type);
      // 重置字段值
      this.message.fields = {};
      // 设置默认值
      this.setDefaultFieldValues(type);
    },
    generateMessageFields(type) {
      switch (type) {
        case 'NewOrderSingle':
          return [
            { tag: 'ClOrdID', name: '客户订单ID' },
            { tag: 'Symbol', name: '交易代码' },
            { tag: 'Side', name: '买卖方向' },
            { tag: 'OrderQty', name: '订单数量' },
            { tag: 'OrdType', name: '订单类型' },
            { tag: 'Price', name: '价格' },
            { tag: 'TimeInForce', name: '有效时间'
            }
          ];
        case 'OrderCancelRequest':
          return [
            { tag: 'OrigClOrdID', name: '原始订单ID' },
            { tag: 'ClOrdID', name: '新订单ID' },
            { tag: 'Symbol', name: '交易代码' },
            { tag: 'Side', name: '买卖方向' },
            { tag: 'OrderQty', name: '订单数量'
            }
          ];
        default:
          return [];
      }
    },
    setDefaultFieldValues(type) {
      // 生成唯一ClOrdID
      if (['NewOrderSingle', 'OrderCancelRequest'].includes(type)) {
        this.message.fields.ClOrdID = 'ORD-' + Date.now();
      }
      // 订单取消请求需要原始订单ID
      if (type === 'OrderCancelRequest') {
        this.message.fields.OrigClOrdID = '';
      }
      // 设置默认买卖方向为买入
      if (['NewOrderSingle', 'OrderCancelRequest'].includes(type)) {
        this.message.fields.Side = '1'; // 1=买入
      }
      // 设置默认订单类型为限价单
      if (type === 'NewOrderSingle') {
        this.message.fields.OrdType = '2'; // 2=限价单
        this.message.fields.TimeInForce = '0'; // 0=当日有效
      }
    },
    sendMessage() {
      if (!this.message.messageType) {
        this.$message.error('请选择消息类型');
        return;
      }

      const messageData = {
        messageType: this.message.messageType,
        fields: this.message.fields
      };

      this.$store.dispatch('sendFixMessage', {
        sessionId: this.currentSessionId,
        message: messageData
      }).then(() => {
        this.$message.success('消息发送成功');
      }).catch(error => {
        this.$message.error(`发送失败: ${error.response.data}`);
      });
    },
    resetMessageForm() {
      this.message = {
        messageType: '',
        fields: {}
      };
      this.messageFields = [];
    },
    addCustomField() {
      this.messageFields.push({
        tag: 'Custom' + Date.now(),
        name: '自定义字段'
      });
    }
  }
};
</script>

<style scoped>
.message-editor-container {
  padding: 20px;
}
.message-field {
  margin-bottom: 10px;
}
</style>