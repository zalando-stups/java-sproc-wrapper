package org.zalando.sprocwrapper.example;

import jakarta.validation.constraints.NotNull;

import org.zalando.sprocwrapper.SProcCall;
import org.zalando.sprocwrapper.SProcCall.Validate;
import org.zalando.sprocwrapper.SProcParam;
import org.zalando.sprocwrapper.SProcService;

/**
 * @author  carsten.wolters
 */
@SProcService(validate = true)
public interface ExampleValidationSProcService {
    @SProcCall
    ExampleDomainObjectWithValidation testSprocCallWithValidation1(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall(validate = Validate.AS_DEFINED_IN_SERVICE)
    ExampleDomainObjectWithValidation testSprocCallWithValidation2(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall(validate = Validate.YES)
    ExampleDomainObjectWithValidation testSprocCallWithValidation3(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall(validate = Validate.NO)
    ExampleDomainObjectWithValidation testSprocCallWithoutValidation(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall
    ExampleDomainObjectWithValidation testSprocCallWithMultipleParametersValidation(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation,
            @SProcParam @NotNull String parameter0, @SProcParam @NotNull String parameter1,
            @SProcParam String parameter2);
}
