package cn.router.werouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/28
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Provider {
    String path();
}
