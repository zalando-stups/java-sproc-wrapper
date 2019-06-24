package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.namedresult.transformer.GenderCodeTransformer;

public class ClassWithGenderCode {

    @DatabaseField(name = "male_female", transformer = GenderCodeTransformer.class)
    private GenderCode genderCode;

    public GenderCode getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(final GenderCode genderCode) {
        this.genderCode = genderCode;
    }

}
