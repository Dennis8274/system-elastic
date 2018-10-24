package org.zhare.design.retry.policy;

import org.zhare.design.retry.RetryContext;
import org.zhare.design.retry.RetryContextSupport;
import org.zhare.design.retry.classify.Classifier;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-19 18:16
 */
public class SimpleRetryPolicy implements RetryPolicy {

    private static final int MAX_ATTEMPTS = 3;

    private volatile int maxAttempts;

    private Classifier<Throwable, Boolean> classifier;

    public SimpleRetryPolicy() {
        this.maxAttempts = MAX_ATTEMPTS;
    }

    public void setClassifier(Classifier<Throwable, Boolean> classifier) {
        this.classifier = classifier;
    }

    public SimpleRetryPolicy(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public boolean canRetry(RetryContext context) {
        Throwable t = context.getLastThrowable();
        return (t != null && shouldRetryFor(t)) && context.getRetryCount() <= maxAttempts;
    }

    private boolean shouldRetryFor(Throwable throwable) {
        if (classifier != null) {
            return classifier.classify(throwable);
        }

        return false;
    }

    @Override
    public RetryContext open() {
        return new SimpleRetryContext();
    }

    @Override
    public void registerThrowable(RetryContext context, Throwable e) {
        SimpleRetryContext simpleContext = ((SimpleRetryContext) context);
        simpleContext.registerThrowable(e);
    }

    @Override
    public void close(RetryContext context) {

    }

    @Override
    public boolean rollbackFor(Throwable lastThrowable) {
        return false;
    }

    private static class SimpleRetryContext extends RetryContextSupport {
    }
}
