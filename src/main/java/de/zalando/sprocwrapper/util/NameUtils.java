package de.zalando.sprocwrapper.util;

import java.util.Locale;

import com.google.common.base.Preconditions;

/**
 * Static utility methods for naming conventions.
 *
 * @author  pribeiro
 */
public final class NameUtils {

    private NameUtils() { }

    public static String camelCaseToUnderscore(final String camelCase) {
        Preconditions.checkNotNull(camelCase, "camelCase");

        final String[] camelCaseParts = org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase(camelCase);
        for (int i = 0; i < camelCaseParts.length; i++) {
            camelCaseParts[i] = camelCaseParts[i].toLowerCase(Locale.ENGLISH);
        }

        return org.apache.commons.lang.StringUtils.join(camelCaseParts, "_");
    }

}
