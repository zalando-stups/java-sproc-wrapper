package de.zalando.sprocwrapper.globalvaluetransformer;

import java.util.Set;

import org.reflections.Reflections;

import org.reflections.scanners.TypeAnnotationsScanner;

import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;

import de.zalando.typemapper.core.ValueTransformer;
import de.zalando.typemapper.core.fieldMapper.GlobalValueTransformerRegistry;

public class GlobalValueTransformerLoader {

    private static final String GLOBAL_VALUE_TRANSFORMER_SEARCH_NAMESPACE = "global.value.transformer.search.namespace";

    // you need to set the namespace to a valid value like: org.doodlejump
    private static String namespaceToScan = "de.zalando";

    private static final Logger LOG = LoggerFactory.getLogger(GlobalValueTransformerLoader.class);
    private static boolean scannedClasspath = false;

    public static synchronized ValueTransformer<?, ?> getValueTransformerForClass(final Class<?> genericType)
        throws InstantiationException, IllegalAccessException {

        // did we already scanned the classpath for global value transformers?
        if (scannedClasspath == false) {
            final Predicate<String> filter = new Predicate<String>() {
                @Override
                public boolean apply(final String input) {
                    return GlobalValueTransformer.class.getCanonicalName().equals(input);
                }
            };

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

            if (!Strings.isNullOrEmpty(myNameSpaceToScan)) {
                final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(
                            new FilterBuilder.Include(FilterBuilder.prefix(myNameSpaceToScan))).setUrls(
                            ClasspathHelper.forPackage(myNameSpaceToScan)).setScanners(new TypeAnnotationsScanner()
                                .filterResultsBy(filter)));
                final Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(
                        GlobalValueTransformer.class);
                for (final Class<?> foundGlobalValueTransformer : typesAnnotatedWith) {
                    final Class<?> valueTransformerReturnType = ValueTransformerUtils.getUnmarshalFromDbClass(
                            foundGlobalValueTransformer);
                    if (valueTransformerReturnType != null) {
                        GlobalValueTransformerRegistry.register(valueTransformerReturnType,
                            (ValueTransformer<?, ?>) foundGlobalValueTransformer.newInstance());
                        LOG.debug("Global Value Transformer [{}] for type [{}] registered. ",
                            foundGlobalValueTransformer.getSimpleName(), valueTransformerReturnType.getSimpleName());
                    } else {
                        LOG.error(
                            "Could add global transformer [{}] to global registry. Could not find method unmarshalFromDb.",
                            foundGlobalValueTransformer);
                    }
                }
            }

            scannedClasspath = true;
        }

        return GlobalValueTransformerRegistry.getValueTransformerForClass(genericType);
    }

    /**
     * Use this static function to set the namespace to scan.
     *
     * @param  newNamespace  the new namespace to be searched for {@link GlobalValueTransformer}
     */
    public static void changeNamespaceToScan(final String newNamespace) {
        namespaceToScan = newNamespace;
        scannedClasspath = false;
    }
}
