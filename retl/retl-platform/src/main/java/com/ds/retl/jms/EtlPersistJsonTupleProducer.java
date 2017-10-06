package com.ds.retl.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jms.JmsTupleProducer;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.mx.StringUtils;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Created by john on 2017/9/6.
 */
public class EtlPersistJsonTupleProducer implements JmsTupleProducer {
    private static final Log logger = LogFactory.getLog(EtlPersistJsonTupleProducer.class);

    public EtlPersistJsonTupleProducer() {
        super();
    }

    @Override
    public Values toTuple(Message msg) throws JMSException {
        if(msg instanceof TextMessage){
            String json = ((TextMessage) msg).getText();
            if (StringUtils.isBlank(json)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("RECEIVE TEXT MESSAGE, but the message is blank.");
                }
                return null;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("RECEIVE TEXT MESSAGE: %s.", json));
            }
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject managedJson = jsonObject.getJSONObject("managedJson");
            JSONObject data = jsonObject.getJSONObject("data");
            if (managedJson == null || data == null) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The JSON data not include managedJson and data: %s.", json));
                }
                return null;
            }
            return new Values(managedJson, data);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("RECEIVE NON TEXT MESSAGE, id: %s, type: %s.",
                        msg.getJMSMessageID(), msg.getJMSType()));
            }
            return null;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("managedJson", "data"));
    }
}
