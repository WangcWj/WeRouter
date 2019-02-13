package cn.router.wang.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.router.api.router.WeRouter;
import cn.router.werouter.annotation.Router;


@Router(path = "native://MainActivity")
public class MainActivity extends AppCompatActivity implements MduleMethod{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //native://MainActivity&name=wang&tag=Chao 带参数的
                //native://MainActivity   不带参数的
                WeRouter.getInstance()
                        .build("native://SecondActivity&name=wang&tag=Chao")
                        .navigation(MainActivity.this);

                Object navigation = WeRouter.getInstance().build("native://BlankFragment").navigation();
                Log.e("WeRouter","MainActivity.onClick."+navigation );
            }
        });
    }
}
