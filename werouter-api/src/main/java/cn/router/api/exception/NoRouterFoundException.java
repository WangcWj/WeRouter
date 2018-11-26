package cn.router.api.exception;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/23
 */

public class NoRouterFoundException extends RuntimeException {
    /**
     * @param detailMessage the detail message for this exception.
     */
    public NoRouterFoundException(String detailMessage) {
        super(detailMessage);
    }
}
