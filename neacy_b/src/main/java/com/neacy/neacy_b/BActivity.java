package com.neacy.neacy_b;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.component.annotation.NeacyInitMethod;
import com.component.annotation.NeacyParam;
import com.component.annotation.NeacyProtocol;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/21
 */
@NeacyProtocol("/activity/b")
public class BActivity extends AppCompatActivity {


    @NeacyParam("string_key")
    public String result_string;
    @NeacyParam("int_key")
    public int result_int;
    @NeacyParam("boolean_key")
    public boolean result_boolean;
    @NeacyParam("long_key")
    public long result_long;
    @NeacyParam("double_key")
    public double result_double;
    @NeacyParam("float_key")
    public float result_float;

    @NeacyInitMethod
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_layout);
        initView();

        Log.w("Jayuchou", "=== string_key = " + result_string);
        Log.w("Jayuchou", "=== int_key = " + result_int);
        Log.w("Jayuchou", "=== boolean_key = " + result_boolean);
        Log.w("Jayuchou", "=== long_key = " + result_long);
        Log.w("Jayuchou", "=== double_key = " + result_double);
        Log.w("Jayuchou", "=== float_key = " + result_float);
    }



    private void initView() {

    }
}
