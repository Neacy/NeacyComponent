package com.component;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块数据传递
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/19
 */
public class ComponentController {

    private static Map<String, IComponent> components = new HashMap<>();

    /**
     * 注册模块
     */
    static void registerComponent(IComponent component) {
        components.put(component.getName(), component);
    }

    public static Map<String, IComponent> getComponents() {
        Log.w("Jayuchou", "==== components = " + components.size());
        return components;
    }

    public static IComponent getComponentByName(String name) {
        return components.get(name);
    }
}
