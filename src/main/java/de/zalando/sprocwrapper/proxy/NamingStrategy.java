package de.zalando.sprocwrapper.proxy;

/**
 * @author  Jan
 */
public interface NamingStrategy {
    String getPGProcedureNameFromJavaName(String methodName);

    String getPGTypeNameFromJavaName(String typeName);
}
