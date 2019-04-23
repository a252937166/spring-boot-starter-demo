# spring-boot-starter

`spring-boot`可以省略众多的繁琐配置，它的众多`starter`可以说是功不可没。
例如`spring-boot`中集成`redis`，只需要`pom.xml`中引入`spring-boot-starter-data-redis`，配置文件中加入`spring.redis.database`等几个关键配置项即可，常用的`starter`还有`spring-boot-starter-web`、`spring-boot-starter-test`、`spring-boot-starter-jdbc`，相比于传统的`xml`配置可以说是大大减少了集成的工作量。

# 原理

利用`starter`实现自动化配置只需要两个条件——`maven`依赖、配置文件，这里简单介绍下`starter`实现自动化配置的流程。
引入`maven`实质上就是导入`jar`包，`spring-boot`启动的时候会找到`starter` `jar`包中的`resources/META-INF/spring.factories`文件，根据`spring.factories`文件中的配置，找到需要自动配置的类，如下：

```java
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnBean({DataSource.class})
@EnableConfigurationProperties({MybatisProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class MybatisAutoConfiguration {
//....
}
```
这是一个`mybatis-spring-boot-autoconfigure`中的自动配置类。
简单说明一下其中的注解：

| 注解|     说明| 
| :-------- | --------:|
| @Configuration|表明是一个配置文件，被注解的类将成为一个bean配置类  | 
| @ConditionalOnClass | 当`classpath`下发现该类的情况下进行自动配置|
| @ConditionalOnBean| 当`classpath`下发现该类的情况下进行自动配置|
| @EnableConfigurationProperties| 使`@ConfigurationProperties`注解生效|
| @AutoConfigureAfter| 完成自动配置后实例化这个`bean`|

# 实现

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.ouyanglol</groupId>
    <artifactId>starter-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>starter-demo</name>
    <description>spring-boot-starter demo</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Source -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>jar-no-fork</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>

```

`spring-boot-starter`就不用说了，`spring-boot-configuration-processor` 的作用是编译时生成 `spring-configuration-metadata.json` ，在`IDE`中编辑配置文件时，会出现提示。
打包选择`jar-no-fork`，因为这里不需要`main`函数。

## EnableDemoConfiguration

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableDemoConfiguration {
}
```
这个不是必须的，建议有这样一个注释，作为自动配置相关属性的入口。

##  DemoProperties

```java
@Data
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {
    private String name;
    private Integer age;
}
```

`name`和`age`对应`application.properties`里面的`demo.name`和`demo.age`

## DemoAutoConfiguration

```java
@Configuration
@ConditionalOnBean(annotation = EnableDemoConfiguration.class)
@EnableConfigurationProperties(DemoProperties.class)
public class DemoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    DemoService demoService (){
        return new DemoService();
    }

}
```

这里设置自动配置的相关条件，和相关操作，由于这里只想写一个最简单的demo，所以这里只需要简单注入一个`bean`，没有复杂逻辑，实际开发中，这个类是最关键的。

## DemoService

```java
public class DemoService {

    @Autowired
    private DemoProperties demoProperties;

    public void print() {
        System.out.println(demoProperties.getName());
        System.out.println(demoProperties.getAge());
    }
}
```

这里不需要`@Service`，因为已经通过`DemoAutoConfiguration`注入`spring`容器了。

## spring.factories

在`resources/META-INF/`下创建`spring.factories`文件:

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.ouyanglol.starterdemo.config.DemoAutoConfiguration
```
告诉`spring-boot`，启动时需要扫描的类。

# 测试

## pom.xml
本地`mvn install`之后，在新的`spring-boot`项目里面引入
```xml
        <dependency>
            <groupId>com.ouyanglol</groupId>
            <artifactId>starter-demo</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```

## 配置文件
```
demo.name = ooo
demo.age = 11
```

如果使用的是`IDEA`，在编辑时会出现提示。

## 测试

```java
@SpringBootApplication
@EnableDemoConfiguration
public class Demo1Application {

    @Autowired
    private DemoService demoService;

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
    }

    @PostConstruct
    public void test() {
        demoService.print();
    }

}
```
启动`main`函数，控制台会打印出配置文件中的`name`和`age`，一个简单的`spring-boot-starter`就写好了
