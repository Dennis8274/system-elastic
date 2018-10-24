package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-19 17:48
 */
public interface Executor {

    <T, E extends Throwable> T execute(RetryCallback<T, E> callback) throws E;

}
