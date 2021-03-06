package org.mx.spring.config;

import org.mx.spring.InitializeTask;
import org.mx.spring.InitializeTask1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

@Import(SpringConfig.class)
public class TestTaskConfig {
    @Bean(name = "initializeTasks")
    public List<Class<? extends InitializeTask>> initialiseTasks() {
        return Arrays.asList(InitializeTask1.class);
    }
}
