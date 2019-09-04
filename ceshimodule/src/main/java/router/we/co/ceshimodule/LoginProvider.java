package router.we.co.ceshimodule;

import cn.router.api.provider.WeProvider;
import cn.router.werouter.annotation.Router;

/**
 * Created to :
 *
 * @author WANG
 * @date 2019/9/4
 */

@Router(path = "native://provider/loginProvider")
public class LoginProvider implements WeProvider {

    @Override
    public void init() {

    }
}
