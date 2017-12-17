import {logger, parser} from 'mx-app-utils'

const requiredRule = (param) => {
  let {type, msg, trigger} = param || {}
  let rule = {required: true}
  if (type && typeof type === 'string' && type !== '') {
    rule.type = type
  }
  if (msg && typeof msg === 'string' && msg !== '') {
    rule.message = msg
  } else {
    rule.message = '数据字段必须输入数据'
  }
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了必填校验规则： %j', rule)
  return rule
}

const _processExtremeInteger = (min, max) => {
  if (!min || !Number.isInteger(min)) {
    min = -1
  }
  if (!max || !Number.isInteger(max)) {
    max = -1
  }
  return {minValue: min, maxValue: max}
}

const _rangeStringRule = (min, max, msg, trigger) => {
  let rule = {type: 'string'}
  if (!msg && msg !== '') {
    rule.message = msg
  }
  let {minValue, maxValue} = _processExtremeInteger(min, max)
  if (minValue !== -1 && maxValue !== -1) {
    // min max
    rule.min = minValue
    rule.max = maxValue
    if (!rule.message) {
      rule.message = '数据字段的长度应介于[' + minValue + ' - ' + maxValue + ']之间'
    }
  } else if (minValue !== -1) {
    // min
    rule.min = minValue
    if (!rule.message) {
      rule.message = '数据字段的长度应大于[' + minValue + ']'
    }
  } else if (maxValue !== -1) {
    // max
    rule.max = maxValue
    if (!rule.message) {
      rule.message = '数据字段的长度应小于[' + max + ']'
    }
  }
  if (trigger !== null && trigger !== undefined && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建一个文本长度范围校验规则： %j', rule)
  return rule
}

const _rangeNumberRule = (min, max, msg, trigger) => {
  let {minValue, maxValue} = _processExtremeInteger(min, max)
  let numberValidator = (rule, value, callback) => {
    if (minValue !== -1 && maxValue !== -1) {
      if (value > maxValue || value < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应介于[' + minValue + ' - ' + maxValue + ']'
        callback(new Error(message))
      }
    } else if (minValue !== -1) {
      if (value < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应大于[' + minValue + ']'
        callback(new Error(message))
      }
    } else if (maxValue !== -1) {
      if (value > maxValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应小于[' + maxValue + ']'
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'number', validator: numberValidator}
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了一个数值范围规则： %j', rule)
  return rule
}

const _processExtremeDate = (min, max) => {
  if (min && typeof min === 'string') {
    min = parser.parseDate(min)
  } else {
    min = undefined
  }
  if (max && typeof max === 'string') {
    max = parser.parseDate(max)
  } else {
    max = undefined
  }
  return {minValue: min, maxValue: max}
}

const _rangeDateRule = (min, max, msg, trigger) => {
  let {minValue, maxValue} = _processExtremeDate(min, max)
  let dateValidator = (rule, value, callback) => {
    if (minValue !== null && maxValue !== null) {
      if (value.getTime() > maxValue.getTime() || value.getTime() < minValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应介于[' + min + ' - ' + max + ']'
        callback(new Error(message))
      }
    } else if (minValue !== null) {
      if (value.getTime() < minValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应晚于[' + min + ']'
        callback(new Error(message))
      }
    } else if (maxValue !== null) {
      if (value.getTime() > maxValue.getTime()) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值应早于[' + max + ']'
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'date', validator: dateValidator}
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了一个日期范围规则：%j.', rule)
  return rule
}

const _rangeArrayRule = (min, max, msg, trigger) => {
  let {minValue, maxValue} = _processExtremeInteger(min, max)
  let arrayValidator = (rule, value, callback) => {
    if (minValue !== -1 && maxValue !== -1) {
      if (value.length > maxValue || value.length < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值个数应介于[' + minValue + ' - ' + maxValue + ']'
        callback(new Error(message))
      }
    } else if (minValue !== -1) {
      if (value.length < minValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值个数应大于[' + minValue + ']'
        callback(new Error(message))
      }
    } else if (maxValue !== -1) {
      if (value.length > maxValue) {
        let message = (!msg && msg !== '') ? msg : '数据字段的值个数应小于[' + maxValue + ']'
        callback(new Error(message))
      }
    }
    callback()
  }
  let rule = {type: 'array', validator: arrayValidator}
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  logger.debug('创建了一个数组个数范围规则：%j.', rule)
  return rule
}

const rangeRule = (param) => {
  let {type, min, max, msg, trigger} = param || {}
  if (!min && !max) {
    throw new Error('范围校验规则至少要设定最大值或者最小值之间的一个')
  }
  if (!type) {
    type = 'string'
  }
  switch (type) {
    case 'number':
      return _rangeNumberRule(min, max, msg, trigger)
    case 'date':
      return _rangeDateRule(min, max, msg, trigger)
    case 'array':
      return _rangeArrayRule(min, max, msg, trigger)
    case 'string':
    default:
      return _rangeStringRule(min, max, msg, trigger)
  }
}

const emailRule = (param) => {
  let {msg, trigger} = param || {}
  let rule = {type: 'email'}
  if (msg && typeof msg === 'string' && msg !== '') {
    rule.message = msg
  } else {
    rule.message = '电子邮件格式错误'
  }
  if (trigger && typeof trigger === 'string' && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  return rule
}

const customRule = (param) => {
  let {validator, trigger} = param || {}
  let rule = {}
  if (validator && typeof validator === 'function') {
    rule.validator = validator
  } else {
    throw new Error('自定义校验规则需要输入自定义的校验函数')
  }
  if (trigger !== null && trigger !== undefined && trigger !== '') {
    rule.trigger = trigger
  } else {
    rule.trigger = 'blur'
  }
  return rule
}

export default {requiredRule, rangeRule, emailRule, customRule}