package de.zalando.sprocwrapper.customerexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Repository;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.SingleDataSourceProvider;

/**
 * @author  Jan
 */
@Repository
public class CustomerExampleSProcServiceImpl
    extends AbstractSProcService<CustomerExampleSProcService, SingleDataSourceProvider>
    implements CustomerExampleSProcService {

    @Autowired
    public CustomerExampleSProcServiceImpl(
            @Qualifier("testDataCustomerExampleProvider") final SingleDataSourceProvider p) {
        super(p, CustomerExampleSProcService.class);
    }

    @Override
    public Customer loadCustomer(final int id) {
        return sproc.loadCustomer(id);
    }

    @Override
    public Customer createCustomer(final Customer customer) {
        return sproc.createCustomer(customer);
    }

    @Override
    public int addNewAddress(final int customerId, final Address address) {
        return sproc.addNewAddress(customerId, address);
    }
}
