package de.zalando.sprocwrapper.proxy;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * @author  Jan
 */
public class CamelCaseToUnderScoreStrategy implements NamingStrategy {

    @Override
    public String getPGProcedureNameFromJavaName(final String camelCase) {
        final String[] camelCaseParts = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        for (int i = 0; i < camelCaseParts.length; i++) {
            camelCaseParts[i] = camelCaseParts[i].toLowerCase(Locale.ENGLISH);
        }

        return StringUtils.join(camelCaseParts, "_");
    }

    @Override
    public String getPGTypeNameFromJavaName(final String typeName) {
        return getPGProcedureNameFromJavaName(typeName);
    }

}
