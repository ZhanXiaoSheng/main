import Vue from 'vue'
import MxVueElUtils from 'mx-vue-el-utils'
import AppComps from 'mx-vue-el-appcomps'

import myEn from '../assets/lang/en.json'
import myZhCN from '../assets/lang/zh-CN.json'

const MxLocale = MxVueElUtils.MxLocale

MxLocale.mergeMessages(AppComps.AppLocaleMessages)
MxLocale.mergeMessages({
  en: {cas: myEn},
  'zh-CN': {cas: myZhCN}
})

const changeLanguage = (lang) => {
  if (lang && typeof lang === 'string' && lang.length > 0) {
    MxLocale.setLanguage(lang)
    Vue.use(MxVueElUtils, {locale: lang})
    Vue.use(AppComps, {locale: lang})
    if (window && window.localStorage) {
      window.localStorage.setItem('locale', lang)
    }
  }
}

export default MxLocale

export {
  MxLocale,
  changeLanguage
}
