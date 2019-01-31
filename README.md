# WeRouter
一款功能简介的路由框架，适用于组件化项目中各个独立Module之间的Activity Fragment的跳转，具体公功能如下：

* 根据指定路径启动Activity，可携带参数以及返回值，可挎module。
* 根据指定路径获取Fragment对象，可挎module。
* 未完待续......

#### 使用:    目前并未上传到Jcenter仓库。

运行demo或者依赖demo的话一定要确保一下module齐全：

* werouter-annotation
* werouter-compiler
* werouter-api
* werouter_plugin

**一 添加依赖:**   

1.使用的话到module的build.gradle文件里面引入，组件化项目推荐把依赖放到最底层module里面：

```groovy
implementation project(':werouter-api')
annotationProcessor project(':werouter-compiler')
```

2.因为插件发到本地的maven仓库里面了并没有上传到线上所以到项目的总build.gradle里面配置一下:

```groovy
//Project 里面的build.gradle
repositories {    
    maven {
            url uri('./werouter_plugin/src/main/repertory')
        }
    }

 dependencies {
        classpath 'cn.werouter:plugin:1.0.0'
    }
```

3.之后要在app也就是集成所有library的module中引入插件：

```groovy
apply plugin: 'cn.werouter.plugin'
```

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

