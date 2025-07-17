import Vue from 'vue'
import VueRouter from 'vue-router'
// import Dashboard from '../views/Dashboard.vue'
import SessionConfig from '../views/SessionConfig.vue'
import MessageEditor from '../views/MessageEditor.vue'
import MessageMonitor from '../views/MessageMonitor.vue'

Vue.use(VueRouter)

const routes = [
  // {
  //   path: '/',
  //   name: 'Dashboard',
  //   component: Dashboard
  // },
  {
    path: '/session-config',
    name: 'SessionConfig',
    component: SessionConfig
  },
  {
    path: '/message-editor',
    name: 'MessageEditor',
    component: MessageEditor
  },
  {
    path: '/message-monitor',
    name: 'MessageMonitor',
    component: MessageMonitor
  }
]

const router = new VueRouter({
  routes
})

export default router