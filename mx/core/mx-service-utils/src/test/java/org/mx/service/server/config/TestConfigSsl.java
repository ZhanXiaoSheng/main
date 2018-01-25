package org.mx.service.server.config;

import org.mx.service.server.rest.DemoRestResource;
import org.mx.service.server.servlet.DownloadFileServlet;
import org.mx.service.server.websocket.EchoSocket;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@Configuration
@Import(ServerConfig.class)
@PropertySource({"classpath:server-ssl.properties"})
@ComponentScan({"org.mx.service.server.rest", "org.mx.service.server.servlet", "org.mx.service.server.websocket"})
public class TestConfigSsl {
    @Bean(name = "restfulClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(DemoRestResource.class);
    }

    @Bean(name = "servletClassesTest")
    public List<Class<?>> servletClassesTest() {
        return Arrays.asList(DownloadFileServlet.class);
    }

    @Bean(name = "websocketClassesTest")
    public List<Class<?>> websocketClassesTest() {
        return Arrays.asList(EchoSocket.class);
    }
}