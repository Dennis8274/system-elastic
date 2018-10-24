package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-24 14:31
 */
public class RetryTestException extends RuntimeException {
    public RetryTestException(String message) {
        super(message);
    }
}
