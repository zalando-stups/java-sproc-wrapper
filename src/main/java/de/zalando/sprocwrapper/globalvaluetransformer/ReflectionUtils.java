package de.zalando.sprocwrapper.globalvaluetransformer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.List;

import org.springframework.util.Assert;

import com.google.common.collect.Lists;

public class ReflectionUtils {

    /**
     * Attempt to find exactly one {@link Method} on the supplied class with the supplied name Searches all superclasses
     * up to <code>Object</code>.
     *
     * <p>Returns <code>null</code> if no {@link Method} can be found.
     *
     * @param   clazz       the class to introspect
     * @param   name        the name of the method
     * @param   paramTypes  the parameter types of the method (may be <code>null</code> to indicate any signature)
     *
     * @return  the Method object, or <code>null</code> if none found
     */
    public static Method findMethod(final Class<?> clazz, final String name) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");

        Class<?> searchType = clazz;
        final List<Method> foundMethods = Lists.newArrayList();
        while (searchType != null && !searchType.isInterface() && !Modifier.isAbstract(searchType.getModifiers())) {
            final Method[] methods = searchType.getDeclaredMethods();
            for (final Method method : methods) {
                if (name.equals(method.getName()) && !method.isSynthetic() && !method.isBridge()) {
                    foundMethods.add(method);
                }
            }

            searchType = searchType.getSuperclass();
        }

        if (foundMethods.isEmpty()) {
            return null;
        }

        if (foundMethods.size() > 1) {
            throw new IllegalArgumentException("The class " + clazz + " contains more than one methods with name "
                    + name);
        }

        return foundMethods.get(0);
    }
}
