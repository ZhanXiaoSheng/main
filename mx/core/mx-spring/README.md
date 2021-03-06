# mx-utils模块
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

## 安装和引用
*Maven*
```xml
<dependency>
    <groupId>org.mx</groupId>
    <artifactId>mx-spring</artifactId>
    <version>1.1.8</version>
</dependency>
```
*Gradle*
```gradle
compile 'org.mx:mx-spring:1.1.8'
```
## 工具类
- org.mx.spring.utils.SpringContextHolder<br>
基于Spring的`ApplicationContextAware`实现的全局静态的获取Spring IoC容器中Bean的工具类，便于在Spring IoC容器托管之外的应用代码获取并调用Spring IoC容器托管代码。<br>
该工具本身是一个`@Component`，会自动载入到Spring IoC中，在需要的地方可以如下调用：
```java
// 根据接口类型获取服务对象接口
OrgService orgService = SpringContextHolder.getBean(OrgService.class);

// 根据指定名称和接口类型获取服务对象接口
OrgService orgService = SpringContextHolder.getBean("orgService", OrgService.class);
```

## 可配置任务
实现了一个基于Executors的多线程后台任务执行工厂`TaskFactory`，并对任务进行顶层抽象，任务定义接口为`Task`，定义了顶层的任务抽象类`BaseTask`。<br>
任务调度方式分为三种情况：
1. 串行任务<br>
仅执行一次，且设定到其中的任务将会严格按照输入顺序串行执行。
2. 单个异步任务<br>
仅执行一次，且设定到其中的任务将会在独立线程中并行执行，系统不保障任务之间的执行顺序，一般针对后台长周期任务。
3. 可调度任务<br>
可执行一次，也可以周期性执行，必须设定该调度任务的调度参数，包括：执行延时、执行间隔等。

### 样例
```java
// 创建一个任务工厂
TaskFactory factory = new TaskFactory();

// 加入并执行一系列串行任务
factory.addSerialTask(task1)
       .addSerialTask(task2)
       .addSerialTask(task3)
       .runSerialTasks();

// 加入并执行一系列异步任务
factory.addSingleAsyncTask(task1);
factory.addSingleAsyncTask(task2);
factory.addSingleAsyncTask(task3);

// 延迟10秒运行一次一个调度任务
factory.addScheduledTask(task1, new Task.ScheduledConfig(true, 10, TimeUnit.SECONDS));

// 每4个小时运行一次一个调度任务，运行前无延时
factory.addSecheduledTask(task1, Task.ScheduledConfig().H(0, 4));
// 或者
factory.addSecheduledTask(task1, new Task.ScheduledConfig(false, 0, 4, TimeUnit.HOURS);
```

### 应用初始化任务
一般的，应用初始化任务意味着在应用系统初始化过程中被单次执行的任务序列，可能是串行执行的任务，也可能是异步执行的长周期任务。<br>
应用初始化任务基于`TaskFactory`中的串行任务和异步任务实现，可以通过简单的配置（零代码，当然任务代码还是需要你实现的:)）来实现初始化任务的调度。<br>
应用初始化任务定义了一个宿主在Spring IoC中的工厂`InitializeTaskFactory`，并定义了一个初始任务的抽象类`InitializeTask`。
要实现一个任务，你需要：
- 第一步<br>
继承`InitializeTask`抽象类，实现你自己的初始化任务的业务逻辑，如果你的逻辑代码在Spring IoC中，别忘了使用SpringContextHolder:)，因为我们的Task都不在Spring IoC中。代码如：
```java
package org.mx.spring;

public class InitializeTask1 extends InitializeTask {
    public InitializeTask1() {
        super("Initialize task1", false);
    }

    public void invokeTask() {
        System.out.println("Invoke task: ***********.");
    }
}
```
- 第二步<br>
基于Java Config方式（也可以使用传统的XML配置方式，大同小异）将你实现的初始化任务注入到Spring IoC中，*注意*：初始化任务的Bean Name必须为"initializeTasks"。如：
```java
package org.mx.spring.config;

import org.mx.spring.InitializeTask;
import org.mx.spring.InitializeTask1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
```
以上两步，就实现了一个应用初始化任务的工作。<br>
*注意*：初始化应用初始化任务工厂的代码已经封装在`SpringConfig`中，如下所示：
```java
/**
 * 创建初始化任务工厂
 *
 * @return 初始化任务工厂
 */
@Bean
public InitializeTaskFactory initializeTaskFactory() {
    return new InitializeTaskFactory(SpringContextHolder.getBean(ApplicationContext.class));
}
```

## redis支持
- 第一步：配置redis<br>
要使用Redis，必须要设置一个名为`redis.properties`的配置文件，其中内容如：
```properties
redis.enable=false
# 类型：standalone | sentinel | cluster
redis.type=standalone
redis.pool.maxIdle=300
redis.pool.maxWaitMillis=3000
redis.pool.testOnBorrow=true
redis.standalone.host=localhost
redis.standalone.port=6379
redis.standalone.database=0
redis.standalone.password=
redis.sentinel.master=master
redis.sentinel.database=0
redis.sentinel.password=
redis.sentinel.sentinelHostAndPorts[0]=localhost:23679
redis.sentinel.sentinelHostAndPorts[1]=localhost:23680
redis.cluster.maxRedirects=1
redis.cluster.password=
redis.cluster.clusterHostAndPorts[0]=localhost:23679
redis.cluster.clusterHostAndPorts[1]=localhost:23680
```
*注意*：需要将`redis.enable`设置为true，并根据实际情况配置使用redis的类型，即`redis.type`配置项。
- 第二步：引入Redis配置<br>
`redis.properties`配置文件需要在应用创建的配置文件中被引入，如下所示：
```java
@Configuration
@PropertySource({
        "classpath:redis.properties"
})
public class YourApplicationConfig {
    // TODO 你的应用配置代码
}
```
- 最后一步：在应用配置类中引入`SpringRedisConfig`<br>
需要在应用的Java Config类中引入`SpringRedisConfig`，如下所示：
```java
@Import({YourApplicationConfig.class})
public class TestRedisConfig {
}
```
然后在你的应用中就使用获取到RedisTemplate工具类来使用Redis了。

## 缓存
通过配置的方式可以支持：interal、ehcache、redis三种缓存。
- 第一步：配置缓存配置文件<br>
要使用缓存，必须配置`cache.properties`配置文件，其内容如下所示：
```properties
# 类型：internal | ehcache | redis
cache.type=internal

cache.internal.list=test1,test2

cache.ehcache.config=ehcache.xml

cache.redis.list=test1,test2
```
- 第二步：引入缓存配置<br>
`cache.properties`配置文件在应用创建的配置文件中被引入，如下所示：
```java
@Configuration
@PropertySource({
        "classpath:cache.properties"
})
public class YourApplicationConfig {
    // TODO 你的应用配置代码
}
```
- 最后一步：在应用配置类中引入`SpringCacheConfig`<br>
需要在应用的Java Config类中引入`SpringCacheConfig`，如下所示：
```java
@Import({SpringCacheConfig.class})
public class TestCacheConfig {
}
```
如果需要使用基于redis的缓存，还必须引入`SpringRedisConfig`，如下所示：
```java
@Import({
    SpringRedisConfig.class,
    SpringCacheConfig.class
})
public class TestRedisCacheConfig {
}
```
然后在你的应用中就使用`@CacheConfig`、`@Cachable`、`@CachePut`、`@CacheEvict`等注解来使用缓存功能了，是不是接近"零编码"呢？

*详细说明参阅本模块的Javadoc。*