package org.zalando.sprocwrapper.example;

import org.zalando.sprocwrapper.SProcCall;
import org.zalando.sprocwrapper.SProcParam;
import org.zalando.sprocwrapper.SProcService;

import java.util.Date;

/**
 * @author  jmussler
 */
@SProcService
public interface ExampleSProcServiceDateConversion {
    @SProcCall
    Date checkDateWithoutTimeZone(@SProcParam Date date);

    @SProcCall
    Date checkDateWithTimeZone(@SProcParam Date date);

    @SProcCall
    Date checkDateWithoutTimeZoneTransformed(@SProcParam Date date);

    @SProcCall
    ComplexDate checkDateComplexDate(@SProcParam ComplexDate complexDate);
}
