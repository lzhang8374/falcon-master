package org.trex.falcon.rpc.invoker;

import java.lang.reflect.Method;

public class Invoker {

    //目标对象
    private Object target;

    public Invoker(Object target) {
        this.target = target;
    }

    /**
     * 执行
     */
    public Object invoke(String method, Object... paramValues) {
        try {
            Method[] methods = this.target.getClass().getMethods();
            Method m = null;
            for (Method d : methods) {
                if (d.getName().equals(method)) {
                    m = d;
                    break;
                }
            }
            return m.invoke(this.target, paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
