package de.zalando.sprocwrapper.customerexample;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

/**
 * @author  Jan
 */
@SProcService
public interface CustomerExampleSProcService {

    @SProcCall
    Customer loadCustomer(@SProcParam int id);

    @SProcCall
    Customer createCustomer(@SProcParam Customer customer);

    @SProcCall
    int addNewAddress(@SProcParam int customerId, @SProcParam Address address);
}
