package org.zhare.design.retry.classify;


import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 22:58
 */
public class SubclassClassifier<T, C> implements Classifier<T, C> {
    private ConcurrentMap<Class<? extends T>, C> classified = new ConcurrentHashMap<Class<? extends T>, C>();

    private C defaultValue = null;

    @SuppressWarnings("unused")
    public SubclassClassifier() {
        this(null);
    }

    public SubclassClassifier(C defaultValue) {
        this(new HashMap<>(), defaultValue);
    }

    public SubclassClassifier(Map<Class<? extends T>, C> typeMap, C defaultValue) {
        this.classified = new ConcurrentHashMap<>(typeMap);
        this.defaultValue = defaultValue;
    }

    public void setTypeMap(Map<Class<? extends T>, C> map) {
        this.classified = new ConcurrentHashMap<>(map);
    }

    @Override
    public C classify(T classifiable) {
        if (classifiable == null) {
            return defaultValue;
        }

        @SuppressWarnings("unchecked")
        Class<? extends T> exceptionClass = (Class<? extends T>) classifiable.getClass();
        if (classified.containsKey(exceptionClass)) {
            return classified.get(exceptionClass);
        }

        // check for subclasses
        Set<Class<? extends T>> classes = new TreeSet<Class<? extends T>>(new ClassComparator());
        classes.addAll(classified.keySet());
        for (Class<? extends T> cls : classes) {
            if (cls.isAssignableFrom(exceptionClass)) {
                C value = classified.get(cls);
                this.classified.put(exceptionClass, value);
                return value;
            }
        }

        return defaultValue;
    }

    public final C getDefault() {
        return defaultValue;
    }

    public Map<Class<? extends T>, C> getClassified() {
        return classified;
    }

    private static class ClassComparator implements Comparator<Class<?>>, Serializable {
        public int compare(Class<?> arg0, Class<?> arg1) {
            if (arg0.equals(arg1)) {
                return 0;
            }

            if (arg0.isAssignableFrom(arg1)) {
                return 1;
            }

            return -1;
        }
    }
}
