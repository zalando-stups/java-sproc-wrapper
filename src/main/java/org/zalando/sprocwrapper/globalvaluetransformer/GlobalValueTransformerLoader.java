package org.zalando.sprocwrapper.globalvaluetransformer;

import com.google.common.base.Strings;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;
import org.zalando.typemapper.core.ValueTransformer;
import org.zalando.typemapper.core.fieldMapper.GlobalValueTransformerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlobalValueTransformerLoader {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalValueTransformerLoader.class);
    private static final String GLOBAL_VALUE_TRANSFORMER_SEARCH_NAMESPACE = "global.value.transformer.search.namespace";
    private static final String NAMESPACE_SEPARATOR = ";";
    private static String namespaceToScan = "org.zalando";
    private static boolean scannedClasspath = false;

    public static synchronized ValueTransformer<?, ?> getValueTransformerForClass(final Class<?> genericType)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        // did we already scanned the classpath for global value transformers?
        if (!scannedClasspath) {

            // last to get the namespace from the system environment
            String myNameSpaceToScan = null;
            try {
                myNameSpaceToScan = System.getenv(GLOBAL_VALUE_TRANSFORMER_SEARCH_NAMESPACE);
            } catch (final Exception e) {
                // ignore - e.g. if a security manager exists and permissions are denied.
            }

            if (Strings.isNullOrEmpty(myNameSpaceToScan)) {

                // last to use the given namespace
                myNameSpaceToScan = namespaceToScan;
            }

            final Set<String> namespaces = parseNamespaces(myNameSpaceToScan);

            namespaces.add(namespaceToScan);
            LOG.debug("Scan the following packages for {}: {}", GlobalValueTransformer.class.getSimpleName(),
                    namespaces);
            final Set<Class<?>> typesAnnotatedWith = loadAnnotatedTypes(namespaces);

            for (final Class<?> foundGlobalValueTransformer : typesAnnotatedWith) {
                final Class<?> valueTransformerReturnType;
                try {
                    valueTransformerReturnType = ValueTransformerUtils.getUnmarshalFromDbClass(
                            foundGlobalValueTransformer);
                    GlobalValueTransformerRegistry.register(valueTransformerReturnType,
                            (ValueTransformer<?, ?>) foundGlobalValueTransformer.getDeclaredConstructor()
                                                                                .newInstance());
                } catch (final RuntimeException e) {
                    LOG.error("Failed to add global transformer [{}] to global registry.",
                            foundGlobalValueTransformer, e);
                    continue;
                }

                LOG.debug("Global Value Transformer [{}] for type [{}] registered.",
                        foundGlobalValueTransformer.getSimpleName(), valueTransformerReturnType.getSimpleName());
            }

            scannedClasspath = true;
        }

        return GlobalValueTransformerRegistry.getValueTransformerForClass(genericType);
    }

    private static Set<Class<?>> loadAnnotatedTypes(Set<String> namespacesToScan) {
        final Predicate<String> filter = input -> GlobalValueTransformer.class.getCanonicalName().equals(input);
        final Set<Class<?>> result = new HashSet<>();
        for (String namespace : namespacesToScan) {
            final Reflections reflections = new Reflections(
                    new ConfigurationBuilder()
                            .filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(namespace)))
                            .setUrls(ClasspathHelper.forPackage(namespace))
                            .setScanners(new TypeAnnotationsScanner().filterResultsBy(filter),
                                    new SubTypesScanner())
            );
            result.addAll(reflections.getTypesAnnotatedWith(GlobalValueTransformer.class));
        }
        return result;
    }

    /**
     * Use this static function to set the namespace to scan.
     *
     * @param newNamespace the new namespace to be searched for
     *                     {@link org.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer}
     */
    public static void changeNamespaceToScan(final String newNamespace) {
        namespaceToScan = newNamespace;
        scannedClasspath = false;
    }

    static Set<String> parseNamespaces(String inputString) {
        return Arrays.stream(inputString.split(NAMESPACE_SEPARATOR))
                     .map(String::trim)
                     .filter(ns -> !Strings.isNullOrEmpty(ns))
                     .collect(Collectors.toSet());
    }
}
