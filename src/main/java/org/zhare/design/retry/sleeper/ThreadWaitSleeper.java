package org.zhare.design.retry.sleeper;


/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-24 11:28
 */
public class ThreadWaitSleeper implements Sleeper {
    @Override
    public void sleep(long backOffPeriod) throws InterruptedException {
        Thread.sleep(backOffPeriod);
    }
}
