package org.zalando.typemapper.sproctest.result;

import org.zalando.typemapper.annotations.DatabaseField;

public class PrimitiveResult {

    @DatabaseField(name = "id")
    private Integer id;
    @DatabaseField(name = "msg")
    private String msg;

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

}
