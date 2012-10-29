
package de.zalando.sprocwrapper.example;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Repository;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.ArrayDataSourceProvider;

/**
 * @author  jmussler
 */
@Repository
public class ExampleSProcServiceDateConversionImpl
    extends AbstractSProcService<ExampleSProcServiceDateConversion, ArrayDataSourceProvider>
    implements ExampleSProcServiceDateConversion {

    @Autowired
    public ExampleSProcServiceDateConversionImpl(@Qualifier("testDataSourceProvider") final ArrayDataSourceProvider p) {
        super(p, ExampleSProcServiceDateConversion.class);
    }

    @Override
    public Date checkDateWithoutTimeZone(final Date date) {
        return sproc.checkDateWithoutTimeZone(date);
    }

    @Override
    public Date checkDateWithTimeZone(final Date date) {
        return sproc.checkDateWithTimeZone(date);
    }

    @Override
    public Date checkDateWithoutTimeZoneTransformed(final Date date) {
        return sproc.checkDateWithoutTimeZoneTransformed(date);
    }

    @Override
    public ComplexDate checkDateComplexDate(final ComplexDate complexDate) {
        return sproc.checkDateComplexDate(complexDate);
    }

}
