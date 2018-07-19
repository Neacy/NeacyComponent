package com.neacy.component;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/7/18
 */
public class ComponentApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
