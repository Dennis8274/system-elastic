package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 18:42
 */
public interface Classifier<C, T> {

    T classify(C t);

}
