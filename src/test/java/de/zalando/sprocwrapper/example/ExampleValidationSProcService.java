package de.zalando.sprocwrapper.example;

import javax.validation.constraints.NotNull;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcCall.Validate;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

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
