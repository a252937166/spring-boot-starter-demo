package com.ouyanglol.starterdemo.service;

import com.ouyanglol.starterdemo.config.DemoProperties;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: dnouyang
 * @Date: 2019/4/23 14:53
 */
public class DemoService {

    @Autowired
    private DemoProperties demoProperties;

    public void print() {
        System.out.println(demoProperties.getName());
        System.out.println(demoProperties.getAge());
    }
}
