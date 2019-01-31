package cn.router.wang.main;

import android.util.Log;

import cn.router.api.provider.PathReplaceProvider;
import cn.router.werouter.annotation.Router;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/28
 */
@Router(path = "/provider/PathServiceImpl")
public class PathServiceImpl implements PathReplaceProvider {

    @Override
    public void init() {

    }

    @Override
    public String format(String path) {
        Log.e("WANG","PathServiceImpl.format." );
        return path;
    }
}
