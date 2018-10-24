package org.zhare.design.retry;

import org.zhare.design.retry.base.BackOffInterruptedException;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-24 11:48
 */
public abstract class StatelessBackOffPolicy implements BackOffPolicy {

    @Override
    public BackOffContext open() {
        return null;
    }

    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        doBackOff();
    }

    protected abstract void doBackOff() throws BackOffInterruptedException;
}
