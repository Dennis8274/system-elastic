package org.zhare.design.retry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-24 14:03
 */
@RunWith(Parameterized.class)
public class RetryExecutorTest {

    private RetryExecutor simpleExecutor;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection init() {
        RetryExecutor simpleExecutor = new RetryExecutor();
        return Collections.<Object>singletonList(simpleExecutor);
    }

    public RetryExecutorTest(RetryExecutor simpleExecutor) {
        this.simpleExecutor = simpleExecutor;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleExecutor() {
        RetryPolicy retryPolicy = new SimpleRetryPolicy(3);
        BackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        ((SleepingBackOffPolicy<FixedBackOffPolicy>) backOffPolicy).withSleeper(new ThreadWaitSleeper());
        ((FixedBackOffPolicy) backOffPolicy).setBackOffPeriod(1000);

        simpleExecutor.setRetryPolicy(retryPolicy);
        simpleExecutor.setBackOffPolicy(backOffPolicy);
        simpleExecutor.setListeners(Collections.singletonList(new RetryListener() {
            @Override
            public <T, E extends Throwable> boolean listen(RetryContext context, RetryCallback<T, E> callback) {
                System.out.println("retry listening,can do some enhanced work");
                return true;
            }

            @Override
            public <T, E extends Throwable> void onFailed(RetryCallback<T, E> callback, RetryContext context, Throwable e) {
                System.out.println("retry listen failed");
            }

            @Override
            public <T, E extends Throwable> void onFinal(RetryCallback<T, E> callback, RetryContext context) {
                System.out.println("retry listen final");
            }
        }));

        int attempts = 3;
        MockRetryCallback retryCallback = new MockRetryCallback();
        retryCallback.setTotalAttempts(attempts);

        Boolean result = false;
        try {
            result = simpleExecutor.execute(retryCallback, context -> {
                System.out.println("exhausted.recovering");
                return true;
            });
        } catch (Throwable throwable) {
            Assert.assertNotNull(throwable);
            Assert.assertEquals(1, retryCallback.retryCounts);
        }

        Assert.assertTrue(result);
        Assert.assertEquals(attempts, retryCallback.totalAttempts);
    }

    private static class MockRetryCallback implements RetryCallback<Boolean, Throwable> {

        private int retryCounts;

        private int totalAttempts;

        private RetryTestException throwable = new RetryTestException("mock retry exception");

        void setTotalAttempts(int totalAttempts) {
            this.totalAttempts = totalAttempts;
        }

        @Override
        public Boolean retry(RetryContext context) throws RetryTestException {
            // doSomething business
            retryCounts++;
            if (retryCounts < totalAttempts) throw throwable;
            return true;
        }
    }
}
