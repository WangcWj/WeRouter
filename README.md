# WeRouter
一款功能简介的路由框架，适用于组件化项目中各个独立Module之间的Activity Fragment的跳转，具体公功能如下：

* 根据指定路径启动Activity，可携带参数以及返回值，可挎module。
* 根据指定路径获取Fragment对象，可挎module。
* 未完待续......

#### 使用:  

**一 添加依赖:**   



**二 初始化:**

 推荐再Application中初始化:  

```java
WeRouter.init(Application);
```

**三 使用:**

给Activity加注解@Router:

```java
@Router(path = "native://SecondActivity")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        String extra = getIntent().getStringExtra("w");
        Log.e("WANG","SecondActivity.onCreate."+extra );
    }
}
```

跳转:

```java
 WeRouter.getInstance()
         .build("native://SecondActivity")
         .navigation(MainActivity.this);
```

