package de.zalando.typemapper.namedresult.results;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;

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
