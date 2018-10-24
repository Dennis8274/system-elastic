package org.zhare.design.retry.backoff;

import org.zhare.design.retry.sleeper.Sleeper;
import org.zhare.design.retry.sleeper.ThreadWaitSleeper;
import org.zhare.design.retry.base.BackOffInterruptedException;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-24 11:52
 */
public class FixedBackOffPolicy extends StatelessBackOffPolicy implements SleepingBackOffPolicy<FixedBackOffPolicy> {

    private static final long DEFAULT_BACK_OFF_PERIOD = 1000L;

    private volatile long backOffPeriod = DEFAULT_BACK_OFF_PERIOD;

    private Sleeper sleeper = new ThreadWaitSleeper();

    public void setBackOffPeriod(long backOffPeriod) {
        this.backOffPeriod = (backOffPeriod > 0 ? backOffPeriod : 1);
    }

    @Override
    protected void doBackOff() throws BackOffInterruptedException {
        try {
            sleeper.sleep(backOffPeriod);
        } catch (InterruptedException e) {
            throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
        }
    }

    @Override
    public FixedBackOffPolicy withSleeper(Sleeper sleeper) {
        this.sleeper = sleeper;
        return this;
    }
}
