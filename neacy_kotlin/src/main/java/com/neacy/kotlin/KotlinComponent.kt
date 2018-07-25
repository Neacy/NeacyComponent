package com.neacy.kotlin

import android.util.Log
import com.component.ComponentParam
import com.component.IComponent
import com.component.annotation.NeacyComponent

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/7/25
 */
@NeacyComponent("kotlin")
class KotlinComponent : IComponent {
    override fun getName(): String {
        return "kotlin"
    }

    override fun startComponent(param: ComponentParam?) {
        Log.w("Jayuchou", "==== start Kotlin Component ====")
    }
}