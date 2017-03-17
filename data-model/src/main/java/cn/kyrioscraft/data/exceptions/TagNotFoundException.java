package cn.kyrioscraft.data.exceptions;

/**
 * Created by kyrioscraft on 6/15/16.
 */
public class TagNotFoundException extends Exception {

    private static final long serialVersionUID = -9070542392426394574L;
    private String msg;

    public TagNotFoundException() {
        super();
    }

    public TagNotFoundException(String msg) {
        this.msg = System.currentTimeMillis()
                + ": " + msg;
    }

    public String getMsg() {
        return msg;
    }


}
