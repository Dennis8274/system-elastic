package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-19 17:48
 */
public interface RetryCallback<T,E extends Throwable> {

    T retry(RetryContext context) throws E;
}
