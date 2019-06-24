package org.zalando.sprocwrapper.globalobjecttransformer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.sprocwrapper.globalobjecttransformer.annotation.GlobalObjectMapper;
import org.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;
import org.zalando.typemapper.core.fieldMapper.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class GlobalObjectTransformerLoader {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalObjectTransformerLoader.class);

    private static final String DEFAULT_NAMESPACE = "org.zalando";

    private static final String GLOBAL_OBJECT_TRANSFORMER_SEARCH_NAMESPACE =
        "global.object.transformer.search.namespace";

    // you need to set the namespace to a valid value like: org.doodlejump
    private static volatile String namespaceToScan = null;
    private static volatile ImmutableMap<Class<?>, ObjectMapper<?>> register;

    public static <T> ObjectMapper<T> getObjectMapperForClass(final Class<T> genericType) throws InstantiationException,
        IllegalAccessException {
        Preconditions.checkNotNull(genericType, "genericType");

        // performance improvement. Volatile field is read only once in the commons scenario.
        ImmutableMap<Class<?>, ObjectMapper<?>> localRegister = register;
        if (localRegister == null) {

            synchronized (GlobalObjectTransformerLoader.class) {
                localRegister = register;
                if (localRegister == null) {
                    register = localRegister = buildMappers();
                }
            }
        }

        @SuppressWarnings("unchecked")
        final ObjectMapper<T> mapper = (ObjectMapper<T>) localRegister.get(genericType);

        return mapper;
    }

    private static ImmutableMap<Class<?>, ObjectMapper<?>> buildMappers() throws InstantiationException,
        IllegalAccessException {

        final Map<Class<?>, ObjectMapper<?>> mappers = new HashMap<>();

        for (final Class<?> foundGlobalObjectTransformer : findObjectMappers()) {

            if (ObjectMapper.class.isAssignableFrom(foundGlobalObjectTransformer)) {

                final ObjectMapper<?> mapper = (ObjectMapper<?>) foundGlobalObjectTransformer.newInstance();
                final Class<?> valueTransformerReturnType = mapper.getType();

                final ObjectMapper<?> previousMapper = mappers.put(valueTransformerReturnType, mapper);

                if (previousMapper == null) {
                    LOG.debug("Global Object Mapper [{}] for type [{}] registered. ",
                        foundGlobalObjectTransformer.getSimpleName(), valueTransformerReturnType.getSimpleName());
                } else {
                    LOG.error("Found multiple global object mappers for type [{}]. [{}] replaced by [{}]",
                            valueTransformerReturnType, previousMapper.getClass().getSimpleName(),
                            valueTransformerReturnType.getSimpleName());
                }
            } else {
                LOG.error("Object mapper [{}] should extend [{}]", foundGlobalObjectTransformer.getSimpleName(),
                    ObjectMapper.class.getSimpleName());
            }
        }

        return ImmutableMap.copyOf(mappers);

    }

    private static Set<Class<?>> findObjectMappers() {
        final Predicate<String> filter = input -> GlobalObjectMapper.class.getCanonicalName().equals(input);

        final String myNameSpaceToScan = getNameSpace();

        final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(
                    new FilterBuilder.Include(FilterBuilder.prefix(myNameSpaceToScan))).setUrls(
                    ClasspathHelper.forPackage(myNameSpaceToScan)).setScanners(new TypeAnnotationsScanner()
                        .filterResultsBy(filter::test), new SubTypesScanner()));

        return reflections.getTypesAnnotatedWith(GlobalObjectMapper.class);
    }

    private static String getNameSpace() {

        String myNameSpaceToScan = namespaceToScan;

        if (myNameSpaceToScan == null) {
            try {
                myNameSpaceToScan = System.getenv(GLOBAL_OBJECT_TRANSFORMER_SEARCH_NAMESPACE);
            } catch (final Exception e) {

                // ignore - e.g. if a security manager exists and permissions are denied.
                LOG.warn("Could not get the value of environment variable: {}. Using: {}",
                        GLOBAL_OBJECT_TRANSFORMER_SEARCH_NAMESPACE, namespaceToScan, e);
            }

            if (myNameSpaceToScan == null) {
                myNameSpaceToScan = DEFAULT_NAMESPACE;
            }
        }

        return myNameSpaceToScan;
    }

    /**
     * Use this static function to set the namespace to scan.
     *
     * @param  newNamespace  the new namespace to be searched for
     *                       {@link GlobalValueTransformer}
     */
    public static void changeNamespaceToScan(final String newNamespace) {
        namespaceToScan = Preconditions.checkNotNull(newNamespace, "newNamespace");
        register = null;
    }
}
