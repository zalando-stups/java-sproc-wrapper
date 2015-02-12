package de.zalando.sprocwrapper;

import de.zalando.typemapper.sproctest.SprocArrayTestIT;
import org.junit.Assert;
import org.junit.Test;


public class WriteTransactionTest {

    @Test
    public void NONE_ONE_PHASE_TWO_PHASE_should_return_correspond_write_transaction() {
        Assert.assertEquals(SProcService.WriteTransaction.NONE, SProcCall.WriteTransaction.NONE.getServiceWriteTransaction(null));
        Assert.assertEquals(SProcService.WriteTransaction.ONE_PHASE, SProcCall.WriteTransaction.ONE_PHASE.getServiceWriteTransaction(null));
        Assert.assertEquals(SProcService.WriteTransaction.TWO_PHASE, SProcCall.WriteTransaction.TWO_PHASE.getServiceWriteTransaction(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void USER_FROM_SERVICE_should_throw_exception_when_SprocService_is_null() {
        SProcCall.WriteTransaction.USE_FROM_SERVICE.getServiceWriteTransaction(null);
    }

    @Test
    public void USER_FROM_SERVICE_should_return_service_writetransaction() {
        Assert.assertEquals(SProcService.WriteTransaction.ONE_PHASE, SProcCall.WriteTransaction.USE_FROM_SERVICE.getServiceWriteTransaction(SProcService.WriteTransaction.ONE_PHASE));
    }

}