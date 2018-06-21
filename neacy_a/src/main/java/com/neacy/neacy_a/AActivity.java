package com.neacy.neacy_a;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.component.ComponentController;
import com.component.ComponentParam;
import com.component.ICallBack;
import com.component.RouterController;
import com.component.annotation.NeacyProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuzongxu <yuzongxu@xiaoyouzi.com>
 * @since 2018/6/21
 */
@NeacyProtocol("/activity/a")
public class AActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_layout);

        findViewById(R.id.id_AAAAA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("key", "AActivity");
                RouterController.startRouter(AActivity.this, "/activity/b", args);
            }
        });

        findViewById(R.id.id_a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentController.getComponentByName("b").startComponent(null);
            }
        });

        findViewById(R.id.id_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> p = new HashMap<>();
                p.put("callback", new ICallBack() {
                    @Override
                    public void onComponentBack(ComponentParam result) {
                        Log.w("Jayuchou", "==== 运行结果 = " + result.getParam().get("result"));
                    }
                });
                ComponentParam cp = new ComponentParam(p);
                ComponentController.getComponentByName("app").startComponent(cp);
            }
        });
    }
}
