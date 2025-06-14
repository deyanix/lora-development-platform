import { boot } from 'quasar/wrappers';
import VueApexCharts from 'vue3-apexcharts';

export default boot(({ app }) => {
  // @ts-expect-error aaa
  app.use(VueApexCharts);
});
