<template>
  <el-form ref="formSetting" :model="formSetting" :rules="rulesSetting" label-width="120px" class="dialog-form">
    <el-row type="flex">
      <el-col :span="12">
        <el-form-item prop="code" :label="$t('common.code')">
          <el-input :value="accountCode" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="name" :label="$t('common.name')">
          <el-input :value="accountName" :readonly="true"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <el-form-item prop="tools" :label="$t('common.favorite')">
          <mx-choose-tag ref="tag" v-model="formSetting.tools" displayFormat="{label}" @change="handleTagChange"
                         @selected="handleSelected" keyField="path" type="gray" :popover-width="300">
            <div class="dict-tree">
              <el-tree ref="tree" :data="treeData" node-key="path" show-checkbox highlight-current></el-tree>
            </div>
          </mx-choose-tag>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex">
      <el-col :span="24">
        <div class="form-buttons">
          <el-button class="button" @click.native="handleReset">{{$t('common.reset')}}</el-button>
          <el-button class="button" @click.native="handleSubmit">{{$t('common.submit')}}</el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
  import { mapGetters } from 'vuex'
  import { logger } from 'mx-app-utils'
  import { MxFormValidateRules, MxAjax, MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'mx-account-setting',
    props: {
      navData: {
        type: Array,
        default: function () {
          return []
        }
      }
    },
    data () {
      return {
        formSetting: {
          tools: []
        },
        rulesSetting: {
          tools: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.required', [this.$t('common.favorite')])})]
        },
        treeData: []
      }
    },
    computed: {
      ...mapGetters(['loginUser']),
      accountCode () {
        let user = this.loginUser
        return user && user.code ? user.code : ''
      },
      accountName () {
        let user = this.loginUser
        return user && user.name ? user.name : ''
      },
      favoriteTools () {
        let user = this.loginUser
        return user && user.favoriteTools && user.favoriteTools.length > 0 ? user.favoriteTools : []
      },
      roles () {
        let user = this.loginUser
        return user && user.roles && user.roles.length > 0 ? user.roles : []
      }
    },
    methods: {
      prepareFavorityTools (tools, list, favoriteTools) {
        if (list && list instanceof Array && list.length > 0) {
          list.forEach(item => {
            if (favoriteTools.indexOf(item.path) >= 0) {
              tools.push(item)
            }
            if (item.children && item.children.length > 0) {
              this.prepareFavorityTools(tools, item.children, favoriteTools)
            }
          })
        }
      },
      prepareNavData (list, roles, favoriteTools, parent) {
        if (list && list instanceof Array && list.length > 0) {
          list.forEach(item => {
            let {path, name, role} = item
            item.label = this.$t(name)
            item.disabled = (parent && parent.disabled === true) || favoriteTools.indexOf(path) >= 0 ||
              (role && roles.indexOf(role) < 0)
            if (item.children && item.children.length > 0) {
              this.prepareNavData(item.children, roles, favoriteTools, item)
            }
          })
        }
      },
      handleTagChange (tags) {
        this.treeData = []
        this.$nextTick(() => {
          let favoriteTools = []
          let {tools} = this.formSetting
          if (tools && tools.length > 0) {
            tools.forEach(tool => favoriteTools.push(tool.path))
          }
          this.prepareNavData(this.navData, this.roles, favoriteTools)
          this.treeData = this.navData
        })
      },
      handleSelected (done) {
        done(this.$refs['tree'].getCheckedNodes(true))
        this.$refs['tree'].setCheckedKeys([])
      },
      handleReset () {
        this.$refs['formSetting'].resetFields()
      },
      handleSubmit () {
        this.$refs['formSetting'].validate(valid => {
          if (valid) {
            let {id, code, name} = this.loginUser
            let {tools} = this.formSetting
            let favoriteTools = []
            tools.forEach(tool => {
              favoriteTools.push(tool.path)
            })
            let url = '/rest/accounts/' + id + '/personal/change'
            logger.debug('send POST "%s".', url)
            let fnSuccess = (data) => {
              if (data) {
                MxNotify.info(this.$t('pages.account.message.changePersonalSuccess', [code, name]))
                this.$router.push('/')
              }
            }
            let data = {id, favoriteTools}
            MxAjax.post({url, data, fnSuccess})
          } else {
            MxNotify.formValidateWarn()
          }
        })
      }
    },
    mounted () {
      let tools = []
      let user = this.loginUser
      if (user && user.favoriteTools && user.favoriteTools instanceof Array && user.favoriteTools.length > 0) {
        this.prepareFavorityTools(tools, this.navData, user.favoriteTools)
      }
      this.formSetting.tools = tools
      this.handleTagChange()
    }
  }
</script>
