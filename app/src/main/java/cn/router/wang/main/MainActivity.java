package cn.router.wang.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cn.router.api.router.WeRouter;
import cn.router.werouter.annotation.Router;


@Router(path = "/Activity/MainActivity")
public class MainActivity extends AppCompatActivity implements MduleMethod{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeRouter.getInstance()
                        .build("/text/SecondActivity")
                        .withString("w","chao")
                        .navigation(MainActivity.this);
               // Object navigation = WeRouter.getInstance().build("/fragment/BlankFragment").navigation();
               // Log.e("WANG","MainActivity.onClick."+navigation );
            }
        });

    }
}
