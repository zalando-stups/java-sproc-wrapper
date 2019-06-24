package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.List;

public class ClassWithListMembersInheritance {

    @DatabaseField(name = "arr")
    private List<ChildClassWithPrimitives> array;

    public List<ChildClassWithPrimitives> getArray() {
        return array;
    }

    public void setArray(final List<ChildClassWithPrimitives> array) {
        this.array = array;
    }

}
