package com.component;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块之间传递的参数
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/19
 */
public class ComponentParam {

    private Map<String, Object> params;

    public ComponentParam() {
        init();
    }

    public ComponentParam(Map<String, Object> params) {
        this.params = params;
        if (params == null) {
            init();
        }
    }

    private void init() {
        this.params = new HashMap<>();
    }

    public Map<String, Object> getParam() {
        return params;
    }
}
