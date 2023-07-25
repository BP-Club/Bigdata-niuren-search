import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import uView from "uview-ui";

Vue.use(uView);
// 全局混入全局方法，使用:this.方法
import Mixin from '@/utils/mixin.js';
Vue.use(Mixin);
import icons from "@/components/ali-icon-calss/ali-icon-calss.vue"
Vue.component('icons', icons)
import btnButton from "@/components/btn-button/btn-button.vue"
Vue.component('btnButton', btnButton)
Vue.config.productionTip = false


Vue.prototype.domain = "https://test.cloudskys.cn";
App.mpType = 'app'
const app = new Vue({
    ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif