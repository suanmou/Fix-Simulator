import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios';
import VueWebSocket from 'vue-websocket';

// 配置Axios
axios.defaults.baseURL = '/api';
Vue.prototype.$http = axios;

// 安装插件
Vue.use(ElementUI);
Vue.use(VueWebSocket, 'ws://localhost:8080/ws/messages');

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');