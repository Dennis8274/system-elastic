package org.zhare.design.retry;

import org.zhare.design.retry.base.BackOffInterruptedException;
import org.zhare.design.retry.base.RetryInterruptedException;
import org.zhare.design.retry.base.RetryException;

import java.util.List;
import java.util.Objects;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-19 18:10
 */
public class RetryExecutor implements Executor {
    private volatile RetryPolicy policy = new SimpleRetryPolicy(3);
    private volatile RetryListener[] listeners = new RetryListener[0];
    private volatile BackOffPolicy backOffPolicy;

    public void setListeners(List<RetryListener> listeners) {
        Objects.requireNonNull(listeners);
        this.listeners = listeners.toArray(new RetryListener[listeners.size()]);
    }

    public void setRetryPolicy(RetryPolicy policy) {
        this.policy = policy;
    }

    public void setBackOffPolicy(BackOffPolicy policy) {
        this.backOffPolicy = policy;
    }

    public <T, E extends Throwable> T execute(RetryCallback<T, E> callback) throws E {
        return doExecute(callback, null);
    }

    public <T, E extends Throwable> T execute(RetryCallback<T, E> callback, RecoverCallback<T> recoverCallback) throws E {
        return doExecute(callback, recoverCallback);
    }

    private <T, E extends Throwable> T doExecute(RetryCallback<T, E> callback, RecoverCallback<T> recoverCallback) throws E {
        RetryPolicy policy = this.policy;
        BackOffPolicy backOffPolicy = this.backOffPolicy;

        // policy initialise context
        RetryContext context = open(policy);

        try {
            // intercept and enhance context
            boolean passed = doOpenIntercept(context, callback);
            if (!passed) throw new RetryInterruptedException("Retry breaking at first attempt");

            BackOffContext backOffContext = null;
            Object attribute = context.getAttribute("backOffContext");
            if (attribute instanceof BackOffContext) {
                backOffContext = (BackOffContext) attribute;
            }

            if (backOffContext == null) {
                backOffContext = backOffPolicy.open();
                if (backOffContext != null) {
                    context.setAttribute("backOffContext", backOffContext);
                }
            }

            while (policy.canRetry(context)) {
                try {
                    return callback.retry(context);
                } catch (Throwable e) {
                    try {
                        // register throwable
                        registerThrowable(policy, context, e);
                    } catch (Exception ex) {
                        throw new RetryInterruptedException("register throwable failed");
                    } finally {
                        doOnFailedInterceptors(callback, context, e);
                    }
                    // backOff
                    try {
                        backOffPolicy.backOff(backOffContext);
                    } catch (BackOffInterruptedException ex) {
                        throw wrapperIfNecessary(ex);
                    }

                    if (policy.canRetry(context)) {
                        backOffPolicy.backOff(backOffContext);
                    }

                    if (shouldRethrow(policy, context)) {
                        throw wrapperIfNecessary(e);
                    }
                }
            }

            return handleRetryExhausted(recoverCallback, context);
        } catch (Throwable e) {
            throw RetryExecutor.<E>wrapperIfNecessary(e);
        } finally {
            doOnFinalInterceptor(callback, context);
            close(policy, context);
        }
    }

    private <T, E extends Throwable> T handleRetryExhausted(RecoverCallback<T> callback, RetryContext context) throws E {
        context.setAttribute(RetryContext.EXHAUSTED, true);
        if (callback != null) {
            context.setAttribute(RetryContext.RECOVERED, true);
            return callback.recover(context);
        }

        throw RetryExecutor.<E>wrapperIfNecessary(context.getLastThrowable());
    }

    private void close(RetryPolicy policy, RetryContext context) {
        policy.close(context);
        context.setAttribute(RetryContext.CLOSED, true);
    }

    private <E extends Throwable, T> void doOnFinalInterceptor(RetryCallback<T, E> callback, RetryContext context) {
        for (RetryListener listener : listeners) {
            listener.onFinal(callback, context);
        }
    }

    private <E extends Throwable, T> void doOnFailedInterceptors(RetryCallback<T, E> callback, RetryContext context, Throwable e) {
        for (RetryListener listener : listeners) {
            listener.onFailed(callback, context, e);
        }
    }

    private void registerThrowable(RetryPolicy policy, RetryContext context, Throwable e) {
        policy.registerThrowable(context, e);
    }

    private <T, E extends Throwable> boolean doOpenIntercept(RetryContext context, RetryCallback<T, E> callback) {
        boolean result = true;
        for (RetryListener listener : listeners) {
            result = result && listener.listen(context, callback);
        }

        return result;
    }

    private RetryContext open(RetryPolicy policy) {
        return policy.open();
    }

    private boolean shouldRethrow(RetryPolicy policy, RetryContext context) {
        Throwable lastThrowable = context.getLastThrowable();
        return policy.rollbackFor(lastThrowable);
    }

    private static <E extends Throwable> E wrapperIfNecessary(Throwable e) throws RetryException {
        if (e instanceof Error) {
            throw (Error) e;
        } else if (e instanceof Exception) {
            E rethrow = (E) e;
            return rethrow;
        } else {
            throw new RetryException("exception in retry", e);
        }
    }
}
