package cn.router.wang.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.router.werouter.annotation.Router;

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
