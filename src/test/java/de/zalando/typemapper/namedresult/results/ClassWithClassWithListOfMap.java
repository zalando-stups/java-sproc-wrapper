package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

/**
 * @author  Ingolf Wagner <ingolf.wagner@zalando.de>
 */
public class ClassWithClassWithListOfMap {

    @DatabaseField(name = "aa")
    private ClassWithListOfMap classWithListOfMap;

    @DatabaseField(name = "bb")
    private String string;

    public String getString() {
        return string;
    }

    public void setString(final String string) {
        this.string = string;
    }

    public ClassWithListOfMap getClassWithListOfMap() {
        return classWithListOfMap;
    }

    public void setClassWithListOfMap(final ClassWithListOfMap classWithListOfMap) {
        this.classWithListOfMap = classWithListOfMap;
    }
}
