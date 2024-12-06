package org.zalando.sprocwrapper.util;

import com.google.common.base.Preconditions;

import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

/**
 * Static utility methods for naming conventions.
 *
 * @author  pribeiro
 */
public final class NameUtils {

    private NameUtils() { }

    public static String camelCaseToUnderscore(final String camelCase) {

        Preconditions.checkNotNull(camelCase, "camelCase");

        final String[] camelCaseParts = splitByCharacterTypeCamelCase(camelCase);
        for (int i = 0; i < camelCaseParts.length; i++) {
            camelCaseParts[i] = camelCaseParts[i].toLowerCase(Locale.ENGLISH);
        }

        return String.join("_", camelCaseParts);
    }

}
