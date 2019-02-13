package cn.router.api.exception;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/23
 */

public class NoProviderFoundException extends RuntimeException {
    /**
     * @param detailMessage the detail message for this exception.
     */
    public NoProviderFoundException(String detailMessage) {
        super(detailMessage);
    }
}
