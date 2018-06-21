package com.neacy.neacy_b;

import android.util.Log;

import com.component.ComponentParam;
import com.component.IComponent;
import com.component.annotation.NeacyComponent;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/21
 */
@NeacyComponent("b")
public class BComponent implements IComponent {
    @Override
    public String getName() {
        return "b";
    }

    @Override
    public void startComponent(ComponentParam param) {
        Log.w("Jayuchou", "==== start BComponent ====");
    }
}
