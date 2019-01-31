package router.we.co.ceshimodule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.router.werouter.annotation.Router;

@Router(path = "native://LoginActivity")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
}
