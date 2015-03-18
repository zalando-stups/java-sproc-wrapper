package de.zalando.sprocwrapper.proxy;

import de.zalando.sprocwrapper.SProcService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Soroosh Sarabadani
 */
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

    @Test
    public void handle_should_create_strategy() {
        @SProcService(shardStrategy = SubVirtualShardKeyStrategy.class)
        class Simple {

        }
        SProcServiceAnnotationHandler.HandlerResult result = handler.handle(Simple.class);
        Assert.assertEquals(SubVirtualShardKeyStrategy.class, result.getShardKeyStrategy().getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void handle_should_throw_exception_when_strategy_is_not_instantiable() {
        @SProcService(shardStrategy = HellVirtualShardKeyStrategy.class)
        class Simple {

        }
        SProcServiceAnnotationHandler.HandlerResult result = handler.handle(Simple.class);
    }

    @Test
    public void handle_should_set_prefix_when_exist() {
        @SProcService(shardStrategy = SubVirtualShardKeyStrategy.class, namespace = "NS")
        class Simple {

        }
        SProcServiceAnnotationHandler.HandlerResult result = handler.handle(Simple.class);
        Assert.assertEquals("NS_", result.getPrefix());
    }

}