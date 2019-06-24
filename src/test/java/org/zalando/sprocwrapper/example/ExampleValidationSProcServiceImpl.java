
package org.zalando.sprocwrapper.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.zalando.sprocwrapper.AbstractSProcService;
import org.zalando.sprocwrapper.dsprovider.ArrayDataSourceProvider;

/**
 * @author  carsten.wolters
 */
@Repository
public class ExampleValidationSProcServiceImpl
    extends AbstractSProcService<ExampleValidationSProcService, ArrayDataSourceProvider>
    implements ExampleValidationSProcService {

    @Autowired
    public ExampleValidationSProcServiceImpl(@Qualifier("testDataSourceProvider") final ArrayDataSourceProvider p) {
        super(p, ExampleValidationSProcService.class);
    }

    @Override
    public ExampleDomainObjectWithValidation testSprocCallWithValidation1(
            final ExampleDomainObjectWithValidation exampleDomainObjectWithValidation) {
        return sproc.testSprocCallWithValidation1(exampleDomainObjectWithValidation);
    }

    @Override
    public ExampleDomainObjectWithValidation testSprocCallWithValidation2(
            final ExampleDomainObjectWithValidation exampleDomainObjectWithValidation) {
        return sproc.testSprocCallWithValidation2(exampleDomainObjectWithValidation);
    }

    @Override
    public ExampleDomainObjectWithValidation testSprocCallWithValidation3(
            final ExampleDomainObjectWithValidation exampleDomainObjectWithValidation) {
        return sproc.testSprocCallWithValidation3(exampleDomainObjectWithValidation);
    }

    @Override
    public ExampleDomainObjectWithValidation testSprocCallWithoutValidation(
            final ExampleDomainObjectWithValidation exampleDomainObjectWithValidation) {
        return sproc.testSprocCallWithoutValidation(exampleDomainObjectWithValidation);
    }

    @Override
    public ExampleDomainObjectWithValidation testSprocCallWithMultipleParametersValidation(
            final ExampleDomainObjectWithValidation exampleDomainObjectWithValidation, final String parameter0,
            final String parameter1, final String parameter2) {
        return sproc.testSprocCallWithMultipleParametersValidation(exampleDomainObjectWithValidation, parameter0,
                parameter1, parameter2);
    }
}
