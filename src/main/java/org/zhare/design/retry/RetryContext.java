package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-19 17:58
 */
public interface RetryContext extends AttributeAccessor {
    String CLOSED = "context.closed";

    String RECOVERED = "context.recovered";

    String EXHAUSTED = "context.exhausted";

    Throwable getLastThrowable();

    int getRetryCount();

}
