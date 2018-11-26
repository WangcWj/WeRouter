package cn.router.api.exception;

/**
 * Created to :
 *
 * @author WANG
 * @date 2018/11/23
 */

public class HandlerException extends RuntimeException {
    public HandlerException(String detailMessage) {
        super(detailMessage);
    }
}