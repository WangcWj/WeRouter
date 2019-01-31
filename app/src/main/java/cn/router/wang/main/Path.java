package cn.router.wang.main;

import cn.router.api.provider.JsonProvider;
import cn.router.werouter.annotation.Router;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/28
 */
@Router(path = "/provider/Path")
public class Path implements JsonProvider {

    @Override
    public void init() {

    }
}
