package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 22:48
 */
public class RetryContextSupport extends AttributeAccessorSupport implements RetryContext {

    private volatile int count;

    private volatile Throwable lastException;

    @Override
    public Throwable getLastThrowable() {
        return lastException;
    }

    @Override
    public int getRetryCount() {
        return count;
    }

    public void registerThrowable(Throwable throwable) {
        this.lastException = throwable;
        if (throwable != null)
            count++;
    }
}
