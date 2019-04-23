package com.ouyanglol.starterdemo.config;

import com.ouyanglol.starterdemo.annotation.EnableDemoConfiguration;
import com.ouyanglol.starterdemo.service.DemoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: dnouyang
 * @Date: 2019/4/23 14:50
 */
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
