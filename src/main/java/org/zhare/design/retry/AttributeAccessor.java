package org.zhare.design.retry;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 16:54
 */
public interface AttributeAccessor {
    void setAttribute(String name, Object value);

    Object getAttribute(String name);
}
