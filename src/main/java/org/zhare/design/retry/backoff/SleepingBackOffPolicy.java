package org.zhare.design.retry.backoff;

import org.zhare.design.retry.sleeper.Sleeper;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-24 11:38
 */
public interface SleepingBackOffPolicy<T extends SleepingBackOffPolicy<T>> extends BackOffPolicy {

    T withSleeper(Sleeper sleeper);

}
