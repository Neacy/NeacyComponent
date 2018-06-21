package com.neacy.component;

import android.util.Log;

import com.component.ComponentParam;
import com.component.ICallBack;
import com.component.IComponent;

import com.component.annotation.NeacyComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/20
 */
@NeacyComponent("app")
public class AppComponent implements IComponent {

    @Override
    public String getName() {
        return "app";
    }

    @Override
    public void startComponent(ComponentParam param) {
        Log.w("Jayuchou", "==== Start AppComponent ====");
        if (param != null && param.getParam().containsKey("callback")) {
            ICallBack callBack = (ICallBack) param.getParam().get("callback");
            Map<String, Object> results = new HashMap<>();
            results.put("result", "我来自AppComponent");
            ComponentParam cp = new ComponentParam(results);
            callBack.onComponentBack(cp);
        }
    }
}
