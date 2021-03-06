# mx-vue-el-utils
====
<h5>版本： V1.5.5</h5>
一个非常简单使用的面向HTML5的WEB开发的框架模块，使用了VUE 2和Element-UI。目前封装了:

1. 工具类：<br/>
  1.1 ajax 封装了默认错误处理的AJAX请求工具类，基于mx-app-utils中的ajax<br/>
  2.2 notify 封装了常规的info、warn、error、formValidateWarn等提示框工具<br/>
  2.3 formValidateRules 封装了常规的：必填校验、范围校验、电子邮件校验、自定义规则校验等表单校验工具<br/>
2. 组件：
  2.1 form组件：tagNormal和tagCouple两个常规的标签组件；<br/>
  2.2 Icon组件：支持所有material design图标；<br/>
  2.3 PaginatePane组件：支持常规新增、修改、删除、详情、刷新和分页显示的面板组件。<br/>
  2.4 基于Input控件的选择组件（ChooseInput），支持定义显示内容，数据为JSON对象。<br/>
  2.5 基于tag控件的选择组件（ChooseTag），支持定义显示内容，数据为JSON对象。<br/>
  2.6 基于Input控件的密码组件（Password），支持显示或隐藏密码。<br/>
  2.7 基于Cascader控件的树型字典选择组件（DictSelect），数据为JSON对象。<br/>
3. 布局：<br/>
  3.1 normal布局组件(LayoutNormal)：常规的顶栏+左边栏导航布局。<br/>
  3.2 max布局组件（LayoutMax）：最大化操作区，顶栏可隐藏，无边栏导航。<br/>
4. 对话框（DialogPane）<br/>
5. 国际化（MxLocale）

## 安装
    npm i mx-vue-el-utils --save

## 用法
    import {locale, ajax, notify, formValidateRules, ChooseInput, ChooseTag, TagNormal, TagCouple, Icon, DialogPane, LayoutNormal} from ‘mx-vue-el-utils'
    import 'mx-vue-el-utils/mx-vue-el-utils.min.css'

    locale.setLanguage('zhCN')
    Vue.use(ElementUI, {locale: locale.elLocale})
    new Vue({locale.i18n, ...})

## 依赖模块
- mx-app-utils
- vue
- vue-i18n
- Element-UI


## 修改历史
**1.5.5**<br>
1. 根据重构后的AjaxAxios，新加了MxAjaxEl组件。
2. 添加了MaxLayout布局。
3. 重构了布局相关的less样式。

**1.5.4**<br>
1. 添加了公共的翻译内容。
2. 添加了中英国旗资源。
3. 添加了中英文切换功能。

**1.5.0**<br>
1. 重构了多语言翻译，修改了发现的bug。

**1.4.10**<br>
1. 添加了一个密码输入控件，支持显示或隐藏密码内容。
2. 添加了一个基于树的字典选择控件。

**1.4.8**<br>
1. 修改了ChooseTag添加功能中存在性匹配中的bug。
2. 调整了显示框架中的样式。

**1.4.5**<br>
1. 为ChooseTag添加了多选增加和状态改变监视的功能。
2. 修改了发现的bug。
3. 将框架中菜单的多语种移入到底层代码中，外部调用者不需要额外处理。
4. 修改了显示面包屑时的递归算法中的bug，调整了面包屑显示样式。
5. 修改了favorite的拼写错误。

**1.3.21**<br>
1. 修改了框架中快捷工具栏中存在引用通知按钮的bug。
2. 添加了点击账户图标后弹出账户信息和操作菜单。
3. 修改了个性化信息菜单的图标。
4. 对点击账户图标后弹出信息框的显示方式进行了控制。

**1.3.17**<br>
1. 添加了对SSR的支持，去除document相关代码。
2. 调整了相关组件的基础样式。
3. 重新调整了多语种架构。
4. 修改了ajax中错误信息bug。
5. 为MxAjax添加了token功能。
6. 修改了框架中角色组定义错误，快捷工具栏中的bug。
7. 重构了MxAjax中的调用参数定义方式。

**1.3.9**<br>
1. 修改了漏导出MxLocale的bug。

**1.3.8**<br>
1. 修改了NormalLayout中header的toggle图标按钮样式。
2. 修改了本控件范围内的多语种载入方式，直接编译到控件中。

**1.3.7**<br>
1. 修改了生产环境配置脚本，瘦身了生产代码。

**1.3.6**<br>
1. 修改了分页表控件中的样式。

**1.3.5**<br>
1. 修改了对话框状态控制的bug。

**1.3.4**<br>
1. 调整了分页表格控件中按钮集操作bug。
2. 调整了图标按钮的样式。

**1.3.3**<br>
1. 调整了基础控件的样式。

**1.3.2**<br>
1. 修改了NormalLayout中用户快捷工具栏的显示方式。

**1.3.1**<br>
1. 修改了NormalLayout中登录账户的显示和控制方式。

**1.3.0**<br>
1. 根据Vue Npm发布标准，重新调整的代码导出规则，并重构了多语种方式。

**1.2.4**<br>
1. 修改了对话框面板中的按钮禁用规则中的bug。
2. 修改了按钮控件的样式：文本超长截断。

**1.2.3**<br>
1. 为ChooseInput控件添加清除功能（clear）。

**1.2.2**<br>
1. 为PaginatePane组件添加了自定义按钮操作功能。
2. 调整了相关组件中按钮的样式定义。

**1.2.1**<br>
1. 重构了ChooseInput和ChooseTag中确认数据的方法，提供了done函数。
2. 修改了DialogPane中遮罩插入方式。
3. 增加了ChooseInput和ChooseTag中的语言翻译（en和zhCN）。

**1.2.0**<br>
1. 添加了ChooseInput和ChooseTag控件。
2. 修改了图标和按钮控件的样式控制。

**1.1.1**<br>
1. 提供了多语种资源文件的合并功能，在setLanguage方法中添加了...messages参数。

**1.1.0**<br>
1. 增加了国际化支持，默认支持en和zhCN语种。

**1.0.9**<br>
1. 增加了对话框的支持。

**1.0.8**<br>
1. 修改了分页面板中按钮多次触发的问题。

**1.0.4**<br>
1. 修改了ajax不能访问和分页post的数据的问题。

**1.0.1**<br>
1. 调整了输出的css文件的路径。

**1.0.0**<br>
1. 初始版本。
