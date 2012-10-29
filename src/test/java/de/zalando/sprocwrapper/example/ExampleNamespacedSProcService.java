package de.zalando.sprocwrapper.example;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcService;

/**
 * @author  hjacobs
 */
@SProcService(namespace = "examplens")
public interface ExampleNamespacedSProcService {

    @SProcCall
    String test();
}
