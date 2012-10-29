package de.zalando.sprocwrapper.example;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.typemapper.annotations.DatabaseField;

/**
 * @author  carsten.wolters
 */

public class ExampleDomainObjectWithValidation {
    @DatabaseField
    @NotNull
    public String a;

    @DatabaseField(name = "b")
    @Min(4)
    @Max(6)
    @NotNull
    public Integer b;

    public String getA() {
        return a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(final Integer b) {
        this.b = b;
    }

    public ExampleDomainObjectWithValidation() { }

    public ExampleDomainObjectWithValidation(final String _a, final Integer _b) {
        a = _a;
        b = _b;
    }

}
