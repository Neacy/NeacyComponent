package com.neacy.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.component.ComponentController
import com.component.annotation.NeacyInitMethod
import com.component.annotation.NeacyParam
import com.component.annotation.NeacyProtocol
import kotlinx.android.synthetic.main.activity_kotlin.*

/**
 * KotlinActivity
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/7/25
 */
@NeacyProtocol("/activity/kotlin")
class NeacyKotlinActivity : AppCompatActivity() {

    @NeacyParam("string_key")
    var result_string: String? = null
    @NeacyParam("int_key")
    var result_int: Int = 0
    @NeacyParam("boolean_key")
    var result_boolean: Boolean = false
    @NeacyParam("long_key")
    var result_long: Long = 0
    @NeacyParam("double_key")
    var result_double: Double = 0.toDouble()
    @NeacyParam("float_key")
    var result_float: Float = 0.toFloat()

    @NeacyInitMethod
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        mKotlinTextView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                ComponentController.getComponentByName("a").startComponent(null)
            }
        })


        Log.w("Jayuchou", "=== string_key = $result_string")
        Log.w("Jayuchou", "=== int_key = $result_int")
        Log.w("Jayuchou", "=== boolean_key = $result_boolean")
        Log.w("Jayuchou", "=== long_key = $result_long")
        Log.w("Jayuchou", "=== double_key = $result_double")
        Log.w("Jayuchou", "=== float_key = $result_float")
    }
}