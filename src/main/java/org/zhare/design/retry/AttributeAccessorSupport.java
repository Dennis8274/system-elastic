package org.zhare.design.retry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 16:56
 */
public abstract class AttributeAccessorSupport implements AttributeAccessor {
    private Map<String, Object> attributes = new ConcurrentHashMap<>(0);

    @Override
    public void setAttribute(String name, Object value) {
        attributes.putIfAbsent(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
