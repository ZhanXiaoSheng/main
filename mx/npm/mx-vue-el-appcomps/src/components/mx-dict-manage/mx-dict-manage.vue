<template>
  <div>
    <mx-paginate-table ref="paginatePane" v-on:buttonHandle="handleButtonClick">
      <el-table :data="tableData" class="table" :max-height="tableMaxHeight" @current-change="handleCurrentChange"
                highlight-current-row header-row-class-name="table-header">
        <el-table-column prop="code" :label="$t('common.code')" :width="100"></el-table-column>
        <el-table-column prop="name" :label="$t('common.name')" :width="120"></el-table-column>
        <el-table-column prop="createdTime" :label="$t('common.createdTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.createdTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="updatedTime" :label="$t('common.updatedTime')" :width="100">
          <template slot-scope="scope">
            {{parseDatetime(scope.row.updatedTime)}}
          </template>
        </el-table-column>
        <el-table-column prop="operator" :label="$t('common.operator')" :width="130"></el-table-column>
        <el-table-column prop="desc" :label="$t('common.desc')"></el-table-column>
      </el-table>
      <mx-dialog ref="dialogPane" :title="title()" v-on:reset="handleReset" v-on:submit="handleSubmit"
                 class="layout-dialog">
        <el-form ref="formCommon" slot="form" :model="formCommon" :rules="rulesCommon" label-width="80px"
                 class="dialog-form">
          <el-row type="flex">
            <el-col :span="8">
              <el-form-item prop="code" :label="$t('common.code')">
                <el-input v-model="formCommon.code" :readonly="readonly"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="16">
              <el-form-item prop="name" :label="$t('common.name')">
                <el-input v-model="formCommon.name" :readonly="readonly"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row type="flex">
            <el-col :span="24">
              <el-form-item prop="desc" :label="$t('common.desc')">
                <el-input type="textarea" v-model="formCommon.desc" :rows="4" :readonly="readonly"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </mx-dialog>
    </mx-paginate-table>
  </div>
</template>

<script>
  import { logger, formatter } from 'mx-app-utils'
  import { MxFormValidateRules, MxAjax, MxNotify } from 'mx-vue-el-utils'

  export default {
    name: 'mx-dict-manage',
    props: ['module'],
    data () {
      return {
        tableMaxHeight: 540,
        tableData: [],
        operate: 'details',
        selected: null,
        formCommon: this.newDict(),
        rulesCommon: {
          code: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.requiredCode', [this.$t('common.code')])})],
          name: [MxFormValidateRules.requiredRule({msg: this.$t('message.validate.requiredName', [this.$t('common.name')])})]
        }
      }
    },
    computed: {
      readonly () {
        return this.operate === 'details'
      },
      moduleName () {
        return this.$t('pages.' + this.module + '.name')
      }
    },
    methods: {
      title () {
        let module = this.moduleName
        switch (this.operate) {
          case 'add':
            return this.$t('message.dialog.title.add', [module])
          case 'edit':
            return this.$t('message.dialog.title.edit', [module])
          case 'details':
          default:
            return this.$t('message.dialog.title.details', [module])
        }
      },
      newDict () {
        return {id: undefined, code: '', name: '', desc: ''}
      },
      parseDatetime (longDate) {
        if (longDate) {
          return formatter.formatDatetime(longDate)
        } else {
          return this.t('NA')
        }
      },
      refreshData (pagination) {
        let fnSuccess = (pagination, data) => {
          // logger.debug('response, page: %j, data: %j.', pagination, data)
          if (data && data instanceof Array) {
            this.tableData = data
            this.$refs['paginatePane'].setPagination(pagination)
            MxNotify.info(this.$t('message.list.refreshSuccess', [this.moduleName]))
          }
        }
        MxAjax.post({url: '/rest/' + this.module + 's', data: pagination, fnSuccess})
      },
      showData (data, operate) {
        if (!data) {
          logger.error('not set the required data.')
          return
        }
        let {id, code, name, desc} = data
        this.formCommon = {id, code, name, desc}
        this.operate = operate
        this.$refs['dialogPane'].show(operate, '80%')
        logger.debug('show dialog, operate: %s, data: %j.', operate, data)
      },
      handleSubmit () {
        this.$refs['formCommon'].validate(valid => {
          if (valid) {
            let {id, code, name, desc} = this.formCommon
            if (this.operate === 'add') {
              let url = '/rest/' + this.module + 's/new'
              logger.debug('send POST "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  MxNotify.info(this.$t('message.list.addSuccess', [this.moduleName]))
                }
              }
              MxAjax.post({url, data: {id, code, name, desc}, fnSuccess})
            } else if (this.operate === 'edit') {
              let url = '/rest/' + this.module + 's/' + id
              logger.debug('send PUT "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.$refs['dialogPane'].hide()
                  this.refreshData(null)
                  MxNotify.info(this.$t('message.list.editSuccess', [this.moduleName]))
                }
              }
              MxAjax.put({url, data: {id, code, name, desc}, fnSuccess})
            }
          } else {
            MxNotify.formValidateWarn()
          }
        })
      },
      handleReset () {
        this.$refs['formCommon'].resetFields()
      },
      handleButtonClick (operate, pagination) {
        switch (operate) {
          case 'refresh':
            this.refreshData(pagination)
            break
          case 'add':
            this.showData(this.newDict(), operate)
            break
          case 'edit':
          case 'delete':
          case 'details':
            if (!this.selected) {
              MxNotify.info(this.$t('message.list.needChoose', [this.moduleName]))
              break
            }
            if (operate === 'delete') {
              let {id} = this.selected
              let url = '/rest/' + this.module + 's/' + id
              logger.debug('send DELETE "%s".', url)
              let fnSuccess = (data) => {
                if (data) {
                  this.refreshData(pagination)
                  MxNotify.info(this.$t('message.list.deleteSuccess', [this.moduleName]))
                }
              }
              MxAjax.del({url, fnSuccess})
            } else {
              this.showData(this.selected, operate)
            }
            break
        }
      },
      handleCurrentChange (currentRow, oldCurrentRow) {
        logger.debug('The current row changed, current: %j, old: %j.', currentRow, oldCurrentRow)
        this.selected = currentRow
      }
    },
    mounted () {
      if (!this.$isServer) {
        if (this.$el) {
          this.tableMaxHeight = this.$el.clientHeight - 110
        }
      }
      this.refreshData(null)
    }
  }
</script>
