package org.trex.falcon.rpc.model;

public class Response extends Message {

    private int code;
    private Object returnValue;

    public Response(int code, Object returnValue) {
        this.code = code;
        this.returnValue = returnValue;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        return "Response [code=" + code + ", returnValue=" + returnValue + "]";
    }
}
