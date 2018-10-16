package org.zhare.design.circuit;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-14 18:34
 */
public interface CircuitBreaker {

    boolean isOpen();

    void markSuccess();

    void markFailed();

}
