package com.neacy.component;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.component.RouterController;
import com.component.annotation.NeacyInitMethod;
import com.component.annotation.NeacyParam;
import com.component.annotation.NeacyProtocol;
import com.neacy.neacy_a.TestParcelable;

@NeacyProtocol("/activity/app")
public class MainActivity extends AppCompatActivity {

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
    @NeacyParam("parcelable_key")
    public TestParcelable testParcelable;

    @NeacyInitMethod
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterController.startRouter(MainActivity.this, "/activity/a");
            }
        });

        Log.w("Jayuchou", "=== string_key = " + result_string);
        Log.w("Jayuchou", "=== int_key = " + result_int);
        Log.w("Jayuchou", "=== boolean_key = " + result_boolean);
        Log.w("Jayuchou", "=== long_key = " + result_long);
        Log.w("Jayuchou", "=== double_key = " + result_double);
        Log.w("Jayuchou", "=== float_key = " + result_float);
        if (testParcelable != null) {
            Log.w("Jayuchou", "=== parcelable_key = " + testParcelable.toString());
        }
    }
}
