package org.zalando.sprocwrapper;

import java.util.List;

import org.joda.time.DateTime;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.zalando.sprocwrapper.example.AddressPojo;
import org.zalando.sprocwrapper.example.ExampleBitmapShardSProcService;

/**
 * @author  hjacobs
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class ShardingParallelTransactionIT {

    @Autowired
    private ExampleBitmapShardSProcService exampleBitmapShardSProcService;

    @Test
    public void testWritingWithoutTransaction() {
        final String street = "" + DateTime.now().getMillis();
        exampleBitmapShardSProcService.insertAddressParallel(street, "none");

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 5 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 5);
    }

    @Test
    public void testWritingWithoutTransactionFailedShard1() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressParallel(street, "local_test_db1");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 1 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 1);
    }

    @Test
    public void testWritingWithoutTransactionFailedShard2() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressParallel(street, "local_test_db2");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 4 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 4);
    }

    @Test
    public void testWritingWithOnePhaseTransaction() {
        final String street = "" + DateTime.now().getMillis();
        exampleBitmapShardSProcService.insertAddressOnePhaseParallel(street, "none");

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 5 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 5);
    }

    @Test
    public void testWritingWithOnePhaseTransactionFailedShard1() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressOnePhaseParallel(street, "local_test_db1");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 0 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 0);
    }

    @Test
    public void testWritingWithOnePhaseTransactionFailedShard2() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressOnePhaseParallel(street, "local_test_db2");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 0 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 0);
    }

    @Test
    @Ignore(
        "This test only works if two phase commits are enabled in PostgreSQL server by setting max_prepared_transactions > 0 (default is 0!)"
    )
    public void testWritingWithTwoPhaseTransaction() {
        final String street = "" + DateTime.now().getMillis();
        exampleBitmapShardSProcService.insertAddressTwoPhaseParallel(street, "none");

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 5 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 5);
    }

    @Test
    public void testWritingWithTwoPhaseTransactionFailedShard1() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressTwoPhaseParallel(street, "local_test_db1");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 0 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 0);
    }

    @Test
    public void testWritingWithTwoPhaseTransactionFailedShard2() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressTwoPhaseParallel(street, "local_test_db2");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 0 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 0);
    }

    @Test
    public void testWritingWithServiceTransaction() {
        final String street = "" + DateTime.now().getMillis();
        exampleBitmapShardSProcService.insertAddressUseFromServiceParallel(street, "none");

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 5 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 5);
    }

    @Test
    public void testWritingWithServiceTransactionFailedShard1() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressUseFromServiceParallel(street, "local_test_db1");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 1 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 1);
    }

    @Test
    public void testWritingWithServiceTransactionFailedShard2() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressUseFromServiceParallel(street, "local_test_db2");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 4 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 4);
    }

    @Test
    public void testWritingWithTransactionAndReadOnlyFlag() {
        final String street = "" + DateTime.now().getMillis();
        exampleBitmapShardSProcService.insertAddressTwoPhaseReadOnlyParallel(street, "none");

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 5 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 5);
    }

    @Test
    public void testWritingWithTransactionAndReadOnlyFlagFailedShard1() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressTwoPhaseReadOnlyParallel(street, "local_test_db1");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 1 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 1);
    }

    @Test
    public void testWritingWithTransactionAndReadOnlyFlagFailedShard2() {
        final String street = "" + DateTime.now().getMillis();
        boolean gotException = false;
        try {
            exampleBitmapShardSProcService.insertAddressTwoPhaseReadOnlyParallel(street, "local_test_db2");
        } catch (final Exception e) {
            gotException = true;
        }

        Assert.assertTrue(gotException);

        final List<AddressPojo> findAddressesByStreet = exampleBitmapShardSProcService.findAddressesByStreet(street);
        Assert.assertTrue("expected 4 but got: " + findAddressesByStreet.size() + " for: " + street,
            findAddressesByStreet.size() == 4);
    }
}
