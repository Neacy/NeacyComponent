package com.neacy.neacy_a;

import android.util.Log;

import com.component.ComponentParam;
import com.component.IComponent;
import com.component.annotation.NeacyComponent;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/21
 */
@NeacyComponent("a")
public class AComponent implements IComponent {
    @Override
    public String getName() {
        return "a";
    }

    @Override
    public void startComponent(ComponentParam param) {
        Log.w("Jayuchou", "==== start AComponent ====");
    }
}
