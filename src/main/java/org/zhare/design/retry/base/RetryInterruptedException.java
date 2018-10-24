package org.zhare.design.retry.base;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 14:40
 */
public class RetryInterruptedException extends RetryException {
    public RetryInterruptedException(String message) {
        super(message);
    }
}
