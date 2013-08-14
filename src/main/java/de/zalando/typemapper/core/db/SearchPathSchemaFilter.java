package de.zalando.typemapper.core.db;

import java.util.List;

public class SearchPathSchemaFilter {

    private SearchPathSchemaFilter() {
        // do not instantiate
    }

    public static String filter(final List<String> names, final List<String> searchPath) {
        if (names.isEmpty()) {
            throw new IllegalArgumentException("cannot filter empty names list by search path");
        }

        for (final String currentSchema : searchPath) {
            for (final String name : names) {
                if (name.startsWith(currentSchema + ".")) {
                    return name;
                }
            }
        }

        return null;
    }

}
