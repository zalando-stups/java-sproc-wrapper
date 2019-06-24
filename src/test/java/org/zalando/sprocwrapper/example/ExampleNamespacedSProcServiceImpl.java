
package org.zalando.sprocwrapper.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.zalando.sprocwrapper.AbstractSProcService;
import org.zalando.sprocwrapper.dsprovider.ArrayDataSourceProvider;

/**
 * @author  jmussler
 */
@Repository
public class ExampleNamespacedSProcServiceImpl
    extends AbstractSProcService<ExampleNamespacedSProcService, ArrayDataSourceProvider>
    implements ExampleNamespacedSProcService {

    @Autowired
    public ExampleNamespacedSProcServiceImpl(@Qualifier("testDataSourceProvider") final ArrayDataSourceProvider p) {
        super(p, ExampleNamespacedSProcService.class);
    }

    @Override
    public String test() {
        return sproc.test();
    }

}
