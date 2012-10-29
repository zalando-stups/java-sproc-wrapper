package de.zalando.sprocwrapper.example;

import java.util.Date;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

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
