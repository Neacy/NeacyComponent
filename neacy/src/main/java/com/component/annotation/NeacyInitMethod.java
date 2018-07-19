package com.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intent变量初始化注入的地方
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/7/18
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface NeacyInitMethod {
}
