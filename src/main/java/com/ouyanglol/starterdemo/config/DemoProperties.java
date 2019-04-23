package com.ouyanglol.starterdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: dnouyang
 * @Date: 2019/4/23 14:49
 */
@Data
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {
    private String name;
    private Integer age;
}
