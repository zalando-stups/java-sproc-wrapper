package de.zalando.typemapper.namedresult.results;

public enum GenderCode {

    MALE("homme"),
    FEMALE("femme");

    private final String code;

    private GenderCode(final String code) {
        this.code = code;
    }

    public static GenderCode fromCode(final String code) {
        for (final GenderCode genderCode : GenderCode.values()) {
            if (code.equals(genderCode.code)) {
                return genderCode;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }
}
