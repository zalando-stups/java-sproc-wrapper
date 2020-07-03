package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.Embed;

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
