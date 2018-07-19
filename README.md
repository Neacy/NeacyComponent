# NeacyComponent
实现模块间 数据通信/路由传递

## 模块间数据通信
1.声明模块

```
@NeacyComponent("app")
public class AppComponent implements IComponent

@NeacyComponent("a")
public class AComponent implements IComponent

@NeacyComponent("b")
public class BComponent implements IComponent
```

2.实现数据传递

```
ComponentController.getComponentByName("b").startComponent(null);// 直接执行module b中的BComponent  

// 在module a中调用app模块中的 AppComponent并回调数据
Map<String, Object> p = new HashMap<>();  
p.put("callback", new ICallBack() {  
    @Override  
    public void onComponentBack(ComponentParam result) {  
        Log.w("Jayuchou", "==== 运行结果 = " + result.getParam().get("result"));  
    }  
});  
ComponentParam cp = new ComponentParam(p);   
ComponentController.getComponentByName("app").startComponent(cp);
```
3.数据传递原理  
通过gradle插件在编译的时候在ComponentController中的static静态块注入代码 注入后的结果如下:

```
public class ComponentController
{
  static
  {
    registerComponent(new AComponent());
    registerComponent(new BComponent());
    registerComponent(new AppComponent());
  }
  
  private static Map<String, IComponent> components = new HashMap();
  
  static void registerComponent(IComponent component)
  {
    components.put(component.getName(), component);
  }
  .  
  .  
  .
}
```


## 路由跳转
1.声明协议

```
@NeacyProtocol("/activity/a")
public class AActivity extends AppCompatActivity

@NeacyProtocol("/activity/b")
public class BActivity extends AppCompatActivity

@NeacyProtocol("/activity/app")
public class MainActivity extends AppCompatActivity
```
2.实现跳转  

```  
RouterController.startRouter(MainActivity.this, "/activity/a");// 跳转到AActivity

Bundle args = new Bundle();  
args.putString("key", "AActivity");  
RouterController.startRouter(AActivity.this, "/activity/b", args);// 跳转到BActivity并携带bundle参数  
```
3.Intent解析：不在为getIntent()烦恼了

```
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
    、、、
}
```
目前支持以上7种数据传递，我们可以看一眼生成的代码：

```
@NeacyInitMethod
protected void onCreate(Bundle savedInstanceState) {
    this.result_int = this.getIntent().getIntExtra("int_key", 0);
    this.result_float = this.getIntent().getFloatExtra("float_key", 0.0F);
    this.result_boolean = this.getIntent().getBooleanExtra("boolean_key", false);
    this.result_long = this.getIntent().getLongExtra("long_key", 0L);
    this.result_double = this.getIntent().getDoubleExtra("double_key", 0.0D);
    this.result_string = this.getIntent().getStringExtra("string_key");
    this.testParcelable = (TestParcelable)this.getIntent().getParcelableExtra("parcelable_key");
    super.onCreate(savedInstanceState);

    、、、
}
```
很明显的看出来直接往NeacyInitMethod注解的方法中直接插入相关代码


4.路由原理
同上也是通过gradle插件在编译的时候注入路由表，从而根据key直接实现跳转，代码如下：  

```

public class RouterController
{
  static
  {
    addRouter("/activity/a", "com.neacy.neacy_a.AActivity");
    addRouter("/activity/b", "com.neacy.neacy_b.BActivity");
    addRouter("/activity/app", "com.neacy.component.MainActivity");
  }
  
  private static Map<String, String> routers = new HashMap();
  
  public static void addRouter(String key, String value)
  {
    routers.put(key, value);
  }
}

```

### 感谢
<https://github.com/luckybilly/CC>

