package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-19 19:50
 */
public interface RetryListener {

    <T, E extends Throwable> boolean listen(RetryContext context, RetryCallback<T, E> callback);

    <T, E extends Throwable> void onFailed(RetryCallback<T, E> callback, RetryContext context, Throwable e);

    <T, E extends Throwable> void onFinal(RetryCallback<T, E> callback, RetryContext context);
}
