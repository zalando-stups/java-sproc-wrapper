
package de.zalando.sprocwrapper.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Repository;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.ArrayDataSourceProvider;

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
}
