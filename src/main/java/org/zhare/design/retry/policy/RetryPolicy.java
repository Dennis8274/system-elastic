package org.zhare.design.retry.policy;

import org.zhare.design.retry.RetryContext;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-19 17:57
 */
public interface RetryPolicy {

    boolean canRetry(RetryContext context);

    RetryContext open();

    void registerThrowable(RetryContext context, Throwable e);

    void close(RetryContext context);

    boolean rollbackFor(Throwable lastThrowable);
}
