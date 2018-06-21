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
3.路由原理  
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

