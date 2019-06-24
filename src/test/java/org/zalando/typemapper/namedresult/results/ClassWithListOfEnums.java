package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.List;

/**
 * @author  Carsten Wolters <c.wolters@gmx.de>
 */
public class ClassWithListOfEnums {

    @DatabaseField(name = "enum_arr")
    private List<Enumeration> enumList;

    @DatabaseField(name = "str")
    private String str;

    public List<Enumeration> getEnumList() {
        return enumList;
    }

    public void setEnumList(final List<Enumeration> enumList) {
        this.enumList = enumList;
    }

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }
}
