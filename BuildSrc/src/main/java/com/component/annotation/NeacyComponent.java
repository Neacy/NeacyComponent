package com.component.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/20
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface NeacyComponent {
    String value();
}