package org.zalando.sprocwrapper.proxy;

import org.zalando.sprocwrapper.SProcCall;
import org.zalando.sprocwrapper.SProcParam;
import org.zalando.sprocwrapper.SProcService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;


/**
 * @author Soroosh Sarabadani
 */
public class SProcCallHandlerTest {
    private static final SProcServiceAnnotationHandler.HandlerResult DEFAULT_HANDLER_RESULT = new SProcServiceAnnotationHandler.HandlerResult("", new SubVirtualShardKeyStrategy(), false, null);
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

    @Test
    public void NONE_ONE_PHASE_TWO_PHASE_should_return_correspond_write_transaction() {
        Assert.assertEquals(SProcService.WriteTransaction.NONE, SProcCallHandler.mapSprocWriteTransactionToServiceWriteTransaction(SProcCall.WriteTransaction.NONE,DEFAULT_HANDLER_RESULT));
        Assert.assertEquals(SProcService.WriteTransaction.ONE_PHASE,SProcCallHandler.mapSprocWriteTransactionToServiceWriteTransaction(SProcCall.WriteTransaction.ONE_PHASE, DEFAULT_HANDLER_RESULT));
        Assert.assertEquals(SProcService.WriteTransaction.TWO_PHASE, SProcCallHandler.mapSprocWriteTransactionToServiceWriteTransaction(SProcCall.WriteTransaction.TWO_PHASE, DEFAULT_HANDLER_RESULT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapSprocWriteTransactionToServiceWriteTransaction_should_throw_exception_when_SprocService_is_null() {

        SProcCallHandler.mapSprocWriteTransactionToServiceWriteTransaction(SProcCall.WriteTransaction.USE_FROM_SERVICE, DEFAULT_HANDLER_RESULT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapSprocWriteTransactionToServiceWriteTransaction_should_throw_exception_when_sproccall_wirtetransaction_is_null() {
        SProcCallHandler.mapSprocWriteTransactionToServiceWriteTransaction(SProcCall.WriteTransaction.USE_FROM_SERVICE, DEFAULT_HANDLER_RESULT);
    }

    @Test
    public void mapSprocWriteTransactionToServiceWriteTransaction_should_return_service_writetransaction() {
        SProcService.WriteTransaction writeTransaction = SProcCallHandler.mapSprocWriteTransactionToServiceWriteTransaction(SProcCall.WriteTransaction.USE_FROM_SERVICE, new SProcServiceAnnotationHandler.HandlerResult("", new SubVirtualShardKeyStrategy(), false, SProcService.WriteTransaction.ONE_PHASE));
        Assert.assertEquals(SProcService.WriteTransaction.ONE_PHASE,writeTransaction);
    }


}
