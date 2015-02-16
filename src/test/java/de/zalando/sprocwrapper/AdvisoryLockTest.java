package de.zalando.sprocwrapper;

import org.junit.Assert;
import org.junit.Test;

public class AdvisoryLockTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateAdvisoryLockWithWrongName() {
        new SProcCall.AdvisoryLock(SProcCall.AdvisoryLock.NoLock.NAME, SProcCall.AdvisoryLock.NoLock.LOCK_ID + 1);
    }

    @Test
    public void shouldCreateAdvisoryLockWithCorrectName() {
        new SProcCall.AdvisoryLock("TEST_LOCK", 100L);
    }

    @Test
    public void shouldCompareLocksCorrectly() {
        Assert.assertEquals(SProcCall.AdvisoryLock.NoLock.LOCK,
            new SProcCall.AdvisoryLock(SProcCall.AdvisoryLock.NoLock.NAME, SProcCall.AdvisoryLock.NoLock.LOCK_ID));
        Assert.assertNotEquals(SProcCall.AdvisoryLock.LockOne.LOCK,
            new SProcCall.AdvisoryLock(SProcCall.AdvisoryLock.NoLock.NAME, SProcCall.AdvisoryLock.NoLock.LOCK_ID));
    }

}
