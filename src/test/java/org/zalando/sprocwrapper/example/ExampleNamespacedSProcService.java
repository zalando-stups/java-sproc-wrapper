package org.zalando.sprocwrapper.example;

import org.zalando.sprocwrapper.SProcCall;
import org.zalando.sprocwrapper.SProcService;

/**
 * @author  hjacobs
 */
@SProcService(namespace = "examplens")
public interface ExampleNamespacedSProcService {

    @SProcCall
    String test();
}
