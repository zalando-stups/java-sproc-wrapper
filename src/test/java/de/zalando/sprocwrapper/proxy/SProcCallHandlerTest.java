package de.zalando.sprocwrapper.proxy;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.sharding.ShardKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;


public class SProcCallHandlerTest {
    private SProcCallHandler handler;


    @Before
    public void setup() {
        this.handler = new SProcCallHandler();
    }

    @Test
    public void should_find_all_methods_annotated_with_SprocCall() {
        class Sample {
            @SProcCall
            public void a() {
            }

            @SProcCall
            public void b() {
            }

        }

        List<Method> sProcCallAnnotatedMethods = handler.findSProcCallAnnotatedMethods(Sample.class);
        Assert.assertEquals(2, sProcCallAnnotatedMethods.size());


    }

    @Test
    public void handle_should_return_empty_map_when_there_is_no_annotated_method() {
        Map<Method, StoredProcedure> result = handler.handle(String.class, SProcServiceAnnotationHandler.DEFAULT_HANDLER_RESULT);
        Assert.assertEquals(0, result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void handle_should_throw_exception_when_handlerResult_is_null() {
        handler.handle(String.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void handle_should_throw_exception_when_class_is_null() {
        handler.handle(null, SProcServiceAnnotationHandler.DEFAULT_HANDLER_RESULT);
    }

    @Test
    public void handle_should_create_storedprocedure_with_name_X() {
        class Sample {
            @SProcCall(name = "X")
            public void a() {
            }

        }
        List<Method> sProcCallAnnotatedMethods = handler.findSProcCallAnnotatedMethods(Sample.class);

        Map<Method, StoredProcedure> handle = handler.handle(Sample.class, SProcServiceAnnotationHandler.DEFAULT_HANDLER_RESULT);

        Assert.assertEquals("X", handle.get(sProcCallAnnotatedMethods.get(0)).getName());
    }

    @Test
    public void handle_should_create_storedprocedure_with_parameter() {
        class Sample {
            @SProcCall
            public void a(@SProcParam(name = "var") String dummyParameter1) {
            }

        }
        List<Method> sProcCallAnnotatedMethods = handler.findSProcCallAnnotatedMethods(Sample.class);

        Map<Method, StoredProcedure> handle = handler.handle(Sample.class, SProcServiceAnnotationHandler.DEFAULT_HANDLER_RESULT);

        StoredProcedure storedProcedure = handle.get(sProcCallAnnotatedMethods.get(0));
        Assert.assertEquals("?", storedProcedure.getSqlParameterList());
    }

    @Test
    public void handle_should_create_storedprocedure_with_parameterized_parameter() {
        class Sample {
            @SProcCall
            public void a(@SProcParam(name = "var") List<String> dummyParameter1) {
            }

        }
        List<Method> sProcCallAnnotatedMethods = handler.findSProcCallAnnotatedMethods(Sample.class);

        Map<Method, StoredProcedure> handle = handler.handle(Sample.class, SProcServiceAnnotationHandler.DEFAULT_HANDLER_RESULT);

        StoredProcedure storedProcedure = handle.get(sProcCallAnnotatedMethods.get(0));
        Assert.assertEquals("?", storedProcedure.getSqlParameterList());
    }


}