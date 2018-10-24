package org.zhare.design.retry.backoff;

import org.zhare.design.retry.base.BackOffInterruptedException;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 16:49
 */
public interface BackOffPolicy {

    BackOffContext open();

    void backOff(BackOffContext backOffContext) throws BackOffInterruptedException;
}
