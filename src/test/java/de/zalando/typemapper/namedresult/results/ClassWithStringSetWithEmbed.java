package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.Embed;

public class ClassWithStringSetWithEmbed {

    @Embed
    private ClassWithStringSet set;

    public ClassWithStringSet getSet() {
        return set;
    }

    public void setSet(final ClassWithStringSet set) {
        this.set = set;
    }

}
