import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios';

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    activeSessions: {},
    messages: [],
    currentSessionId: null,
    fixMessageTypes: [
      { value: 'NewOrderSingle', label: 'New Order Single' },
      { value: 'OrderCancelRequest', label: 'Order Cancel Request' },
      { value: 'OrderCancelReplaceRequest', label: 'Order Cancel/Replace Request' },
      { value: 'ExecutionReport', label: 'Execution Report' },
      { value: 'OrderStatusRequest', label: 'Order Status Request' }
    ]
  },
  mutations: {
    setActiveSessions(state, sessions) {
      state.activeSessions = sessions;
    },
    addMessage(state, message) {
      state.messages.unshift(message);
      // 限制消息数量，保持性能
      if (state.messages.length > 1000) {
        state.messages.pop();
      }
    },
    setCurrentSessionId(state, sessionId) {
      state.currentSessionId = sessionId;
    }
  },
  actions: {
    // 获取所有会话状态
    fetchSessions({
      commit
    }) {
      return axios.get('/sessions').then(response => {
        commit('setActiveSessions', response.data);
        return response.data;
      });
    },
    // 创建新会话
    createSession({
      dispatch
    }, config) {
      return axios.post('/sessions', config).then(() => {
        dispatch('fetchSessions');
      });
    },
    // 停止会话
    stopSession({
      dispatch
    }, sessionId) {
      return axios.delete(`/sessions/${sessionId}`).then(() => {
        dispatch('fetchSessions');
      });
    },
    // 发送FIX消息
    sendFixMessage(_, { sessionId, message }) {
      return axios.post(`/sessions/${sessionId}/messages`, message);
    }
  },
  getters: {
    filteredMessages: (state) => (sessionId) => {
      if (!sessionId) return state.messages;
      return state.messages.filter(msg => msg.sessionId === sessionId);
    }
  }
})