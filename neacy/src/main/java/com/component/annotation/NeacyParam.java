package com.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 变量赋值
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/7/18
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface NeacyParam {
    String value();
}
