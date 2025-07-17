import Vue from 'vue'
import VueRouter from 'vue-router'
// import Dashboard from '../views/Dashboard.vue'
import SessionConfig from '../views/SessionConfig.vue'
import MessageEditor from '../views/MessageEditor.vue'
import MessageMonitor from '../views/MessageMonitor.vue'
import TestScenarioManager from '@/views/TestScenarioManager';
import TestReport from '@/views/TestReport';
import RegressionTest from '@/views/RegressionTest';
import RegressionReport from '@/views/RegressionReport';

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
  },
  {
    path: '/test-scenarios',
    name: 'TestScenarioManager',
    component: TestScenarioManager
  },
  {
    path: '/test-report/:reportId',
    name: 'TestReport',
    component: TestReport
  },
  {
    path: '/regression-test',
    name: 'RegressionTest',
    component: RegressionTest
  },
  {
    path: '/regression-report/:reportId',
    name: 'RegressionReport',
    component: RegressionReport
  }
]

const router = new VueRouter({
  routes
})

export default router