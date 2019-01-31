package cn.router.werouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created to :Mark the class as output class.
 *
 * @author WANG
 * @since  2018/11/2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface OutPut {

    /**
     * <p>
     *     module对外输出的类路径 将通过该类路径去查找标记类
     * </p>
     * @return
     */
    String path();

}
