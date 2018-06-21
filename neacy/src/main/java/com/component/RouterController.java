package com.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块路由模块
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/21
 */
public class RouterController {

    private static final String TAG = "RouterController";

    private static Map<String, String> routers = new HashMap<>();

    public static void addRouter(String key, String value) {
        routers.put(key, value);
    }

    public static void startRouter(Context context, String key) {
        startRouter(context, key, null);
    }

    /**
     * 执行路由跳转
     */
    public static void startRouter(Context context, String key, Bundle intentParam) {
        String routerString = routers.get(key);
        if (routerString != null) {
            try {
                Class<?> clazz = Class.forName(routerString);
                Intent intent = new Intent(context, clazz);
                if (intentParam != null) {
                    intent.putExtras(intentParam);
                }
                if (context instanceof  Activity) {
                    context.startActivity(intent);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "=== 路由跳转出错了 = " + e.toString());
            }
        } else {
            Log.e(TAG, "=== 路由出错了  找不到路由 = " + key);
        }
    }
}
