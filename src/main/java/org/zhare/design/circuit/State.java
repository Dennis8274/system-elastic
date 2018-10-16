package org.zhare.design.circuit;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-15 22:32
 */
public interface State {

    void markSuccess();

    void markFailed();

    boolean isAvailable();
}
