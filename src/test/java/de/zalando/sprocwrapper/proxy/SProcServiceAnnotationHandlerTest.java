package de.zalando.sprocwrapper.proxy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SProcServiceAnnotationHandlerTest {
    private SProcServiceAnnotationHandler handler;

    @Before
    public void setup() {
        handler = new SProcServiceAnnotationHandler();
    }


    @Test
    public void handle_should_return_DEFAULT_HANDLER_when_class_does_not_have_SproceService_annotation() {
        SProcServiceAnnotationHandler.HandlerResult result = handler.handle(String.class);
        Assert.assertSame(SProcServiceAnnotationHandler.DEFAULT_HANDLER_RESULT, result);
    }

}