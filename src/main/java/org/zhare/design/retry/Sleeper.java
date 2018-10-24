package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-24 11:27
 */
public interface Sleeper {
    void sleep(long backOffPeriod) throws InterruptedException;
}
