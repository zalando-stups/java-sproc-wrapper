package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.Embed;

public class ClassWithEmbed {

    @Embed
    private ClassWithPrimitives primitives;

    @DatabaseField(name = "str")
    private String str;

    public ClassWithPrimitives getPrimitives() {
        return primitives;
    }

    public void setPrimitives(final ClassWithPrimitives primitives) {
        this.primitives = primitives;
    }

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

}
