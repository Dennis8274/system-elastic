package org.zhare.design.retry.base;


/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 17:15
 */
public class BackOffInterruptedException extends RetryException {
    public BackOffInterruptedException(String message) {
        super(message);
    }

    public BackOffInterruptedException(String message, Throwable e) {
        super(message, e);
    }
}
