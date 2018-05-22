package org.mx.dal.config;

import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralAccessorImpl;
import org.mx.dal.service.impl.GeneralDictAccessorImpl;
import org.mx.dal.service.impl.OperateLogServiceImpl;
import org.mx.dal.session.SessionDataStore;
import org.mx.dal.utils.ElasticUtil;
import org.mx.dal.utils.ElasticUtilRest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 描述： 基于Elasticsearch的DAL基础实现
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:40
 */
@Import(DalConfig.class)
@PropertySource({
        "classpath:elastic.properties"
})
public class DalElasticConfig {
    @Bean(name = "elasticUtilRest", initMethod = "init", destroyMethod = "destroy")
    public ElasticUtil elasticUtilRest(Environment env, SessionDataStore sessionDataStore) {
        return new ElasticUtilRest(env, sessionDataStore);
    }

    @Bean(name = "generalAccessorElastic")
    public GeneralAccessor generalAccessorElastic(ElasticUtil elasticUtil) {
        return new GeneralAccessorImpl(elasticUtil);
    }

    @Bean(name = "generalDictAccessorElastic")
    public GeneralDictAccessor generalDictAccessorElastic(ElasticUtil elasticUtil) {
        return new GeneralDictAccessorImpl(elasticUtil);
    }

    @Bean(name = "operateLogServiceElastic")
    public OperateLogService operateLogServiceElastic(ElasticUtil elasticUtil, SessionDataStore sessionDataStore) {
        return new OperateLogServiceImpl(generalAccessorElastic(elasticUtil), sessionDataStore);
    }
}
