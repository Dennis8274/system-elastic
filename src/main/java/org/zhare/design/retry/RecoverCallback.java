package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 16:39
 */
public interface RecoverCallback<T> {
    T recover(RetryContext context);
}
