package de.zalando.typemapper.sproctest.result;

import de.zalando.typemapper.annotations.DatabaseField;

public class PrimitiveResult2 {

    @DatabaseField(name = "id")
    private Integer id;
    @DatabaseField(name = "msg")
    private String msg;
    @DatabaseField(name = "msg2")
    private String msg2;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public String getMsg2() {
        return msg2;
    }

    public void setMsg2(final String msg2) {
        this.msg2 = msg2;
    }

}
