package org.mx.service.test.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.rest.DemoRestResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({"classpath:server-https.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.rest"
})
public class TestHttpsConfig {
    @Bean(name = "restfulClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(DemoRestResource.class);
    }
}
