package org.zhare.design.circuit;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-15 22:13
 */
public class DefaultCircuitBreaker implements CircuitBreaker {
    private final State circuitState;

    public DefaultCircuitBreaker() {
        this.circuitState = new CircuitState();
    }

    public boolean isOpen() {
        return !circuitState.isAvailable();
    }

    public void markSuccess() {
        circuitState.markSuccess();
    }

    public void markFailed() {
        circuitState.markFailed();
    }
}
