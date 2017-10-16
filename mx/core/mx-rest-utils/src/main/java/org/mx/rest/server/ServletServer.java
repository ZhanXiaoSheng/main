package org.mx.rest.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.mx.StringUtils;
import org.mx.rest.server.servlet.BaseHttpServlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Http Servlet服务器
 *
 * @author : john.peng date : 2017/10/6
 * @see InitializingBean
 */
@Component("servletServer")
public class ServletServer implements InitializingBean {
    private static final Log logger = LogFactory.getLog(ServletServer.class);
    @Autowired
    private Environment env = null;
    @Autowired
    private ApplicationContext context = null;
    private Server server = null;

    /**
     * 默认的构造函数
     */
    public ServletServer() {
        super();
    }

    /**
     * 获取Http Servlet服务器
     *
     * @return 服务器
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        String serviceClassesDef = "servlet.service.classes";
        String servletServiceClassesDef = this.env.getProperty(serviceClassesDef);
        if (StringUtils.isBlank(servletServiceClassesDef)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("You not define [%s], will not ", serviceClassesDef));
            }

        } else {
            int port = env.getProperty("servlet.port", Integer.class, 9997);
            Server server = new Server(port);
            ServletContextHandler contextHandler = new ServletContextHandler(1);
            contextHandler.setContextPath("/");
            server.setHandler(contextHandler);
            String[] classesDefs = servletServiceClassesDef.split(",");

            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> servletClasses = (List) this.context.getBean(classesDef, List.class);
                    if (servletClasses != null && !servletClasses.isEmpty()) {
                        servletClasses.forEach((clazz) -> {
                            BaseHttpServlet servlet = (BaseHttpServlet) this.context.getBean(clazz);
                            contextHandler.addServlet(new ServletHolder(servlet), servlet.getPathSpec());
                        });
                    }
                }
            }

            server.start();
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start ServletServer success, listen port: %d.", port));
            }

        }
    }
}

