package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithObjectWithEmbed {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "obj")
    private ClassWithEmbed classWithEmbed;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public ClassWithEmbed getClassWithEmbed() {
        return classWithEmbed;
    }

    public void setClassWithEmbed(final ClassWithEmbed classWithEmbed) {
        this.classWithEmbed = classWithEmbed;
    }
}
