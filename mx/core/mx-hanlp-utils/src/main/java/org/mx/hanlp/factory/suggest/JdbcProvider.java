package org.mx.hanlp.factory.suggest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.error.UserInterfaceHanlpErrorException;
import org.mx.hanlp.factory.SuggesterConfigBean;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 描述： JDBC连接方式导入推荐数据提供器。
 *
 * @author John.Peng
 * Date time 2018/4/20 下午6:22
 */
public class JdbcProvider implements SuggestContentProvider {
    private static final Log logger = LogFactory.getLog(JdbcProvider.class);

    private String driver, url, user, password, query;
    private String idField = "ID";

    /**
     * {@inheritDoc}
     *
     * @see SuggestContentProvider#init(SuggesterConfigBean.SuggesterProviderConfig)
     */
    @Override
    public void init(SuggesterConfigBean.SuggesterProviderConfig providerConfig) {
        SuggesterConfigBean.SuggesterJdbcProviderConfig jdbcProviderConfig =
                (SuggesterConfigBean.SuggesterJdbcProviderConfig) providerConfig;
        driver = jdbcProviderConfig.getDriver();
        url = jdbcProviderConfig.getUrl();
        user = jdbcProviderConfig.getUser();
        password = jdbcProviderConfig.getPassword();
        query = jdbcProviderConfig.getQuery();
        if (StringUtils.isBlank(driver)) {
            throw new UserInterfaceHanlpErrorException(UserInterfaceHanlpErrorException.HanlpErrors.DB_NO_DRIVER);
        }
        if (StringUtils.isBlank(url)) {
            throw new UserInterfaceHanlpErrorException(UserInterfaceHanlpErrorException.HanlpErrors.DB_NO_URL);
        }
        idField = jdbcProviderConfig.getIdField();
    }

    /**
     * {@inheritDoc}
     *
     * @see SuggestContentProvider#loadSuggestContent(ItemSuggester)
     */
    @Override
    public long loadSuggestContent(ItemSuggester itemSuggester) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            Set<String> fields = new HashSet<>(rsmd.getColumnCount());
            for (int index = 1; index <= rsmd.getColumnCount(); index++) {
                fields.add(rsmd.getColumnName(index));
            }
            if (!fields.contains(idField)) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The %s field not existed, please check your config.",
                            idField));
                }
                throw new UserInterfaceHanlpErrorException(UserInterfaceHanlpErrorException.HanlpErrors.DB_MAPPING_ERROR);
            }
            long total = 0;
            while (rs.next()) {
                String id = rs.getString(idField);
                if (StringUtils.isBlank(id)) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("The value of the ID or CONTENT field is null, the data is ignored.");
                    }
                }
                JSONObject json = transform(fields, rs);
                if (logger.isDebugEnabled()) {
                    logger.debug(json.toJSONString());
                }
                itemSuggester.addSuggestItem(ItemSuggester.SuggestItem.valueOf(itemSuggester.getName(),
                        id, json.toJSONString()));
                total++;
            }
            return total;
        } catch (ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The JDBC driver[%s] not existed.", driver));
            }
            throw new UserInterfaceHanlpErrorException(UserInterfaceHanlpErrorException.HanlpErrors.DB_CONNECT_FAIL);
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Connect the db fail, url: %s, user: %s.", url, user));
            }
            throw new UserInterfaceHanlpErrorException(UserInterfaceHanlpErrorException.HanlpErrors.DB_CONNECT_FAIL);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Close the db connection fail.", ex);
                }
            }
        }
    }

    // 将一行数据转换为JSON对象
    private JSONObject transform(Set<String> fields, ResultSet rs) {
        JSONObject json = new JSONObject();
        for (String field : fields) {
            try {
                String value = rs.getString(field);
                json.put(field, value);
            } catch (SQLException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Get the field[%s] fail.", field));
                }
            }
        }
        return json;
    }
}
