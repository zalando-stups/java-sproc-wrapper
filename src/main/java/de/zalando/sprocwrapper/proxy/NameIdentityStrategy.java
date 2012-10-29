package de.zalando.sprocwrapper.proxy;

/**
 * @author  Jan
 */
public class NameIdentityStrategy implements NamingStrategy {

    @Override
    public String getPGProcedureNameFromJavaName(final String methodName) {
        return methodName;
    }

    @Override
    public String getPGTypeNameFromJavaName(final String typeName) {
        return typeName;
    }

}
