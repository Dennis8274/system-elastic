package org.zhare.design.retry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-23 23:10
 */
public class BooleanClassifier extends SubclassClassifier<Throwable, Boolean> {

    private boolean traverseCauses;

    public BooleanClassifier(boolean defaultValue) {
        super(defaultValue);
    }

    public BooleanClassifier(Collection<Class<? extends Throwable>> exceptionClasses, boolean value) {
        this(!value);
        if (exceptionClasses != null) {
            Map<Class<? extends Throwable>, Boolean> map = new HashMap<Class<? extends Throwable>, Boolean>();
            for (Class<? extends Throwable> type : exceptionClasses) {
                map.put(type, !getDefault());
            }
            setTypeMap(map);
        }
    }

    public BooleanClassifier(Map<Class<? extends Throwable>, Boolean> typeMap, boolean defaultValue) {
        super(typeMap, defaultValue);
    }

    public void setTraverseCauses(boolean traverseCauses) {
        this.traverseCauses = traverseCauses;
    }

    @Override
    public Boolean classify(Throwable classifiable) {
        Boolean classified = super.classify(classifiable);
        if (!this.traverseCauses) {
            return classified;
        }

        if (classified.equals(this.getDefault())) {
            Throwable cause = classifiable;
            do {
                if (this.getClassified().containsKey(cause.getClass())) {
                    return classified; // non-default classification
                }
                cause = cause.getCause();
                classified = super.classify(cause);
            }
            while (cause != null && classified.equals(this.getDefault()));
        }

        return classified;
    }
}
