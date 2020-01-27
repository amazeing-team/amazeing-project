import Vue from 'vue';
import Vuetify from 'vuetify/lib';
import de from 'vuetify/es5/locale/de';
import '@mdi/font/css/materialdesignicons.css'; // Ensure you are using css-loader

Vue.use(Vuetify);

export default new Vuetify({
  lang: {
    locales: { de },
    current: 'de',
  },
  icons: {
    iconfont: 'mdi',
  },
});
