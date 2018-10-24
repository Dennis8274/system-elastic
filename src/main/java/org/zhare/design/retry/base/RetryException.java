package org.zhare.design.retry.base;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 15:06
 */
public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable e) {
        super(message, e);
    }
}
