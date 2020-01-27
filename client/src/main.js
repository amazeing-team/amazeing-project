import Vue from 'vue';
import Axios from 'axios';
import VueCookie from 'vue-cookie';
import VueI18n from 'vue-i18n';
import VueNativeSock from './vue-native-websocket/Main';
import router from './router/index';
import vuetify from './plugins/vuetify';
import App from './App.vue';
import store from './store/store';
import i18n from './plugins/i18n';

Vue.prototype.$http = Axios;

Vue.use(VueNativeSock, 'ws://localhost:8081', {
  store,
  format: 'json',
  connectManually: true,
});

Vue.use(VueI18n);
Vue.use(VueCookie);

Vue.config.productionTip = false;

i18n.locale = navigator.language;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  vuetify,
  store,
  components: { App },
  i18n,
  template: '<App/>',
});
