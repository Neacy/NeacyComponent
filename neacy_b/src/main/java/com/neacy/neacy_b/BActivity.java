package com.neacy.neacy_b;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.component.annotation.NeacyProtocol;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/21
 */
@NeacyProtocol("/activity/b")
public class BActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_layout);

        String result= getIntent().getStringExtra("key");
        Log.w("Jayuchou", "=== result = " + result);
    }
}
