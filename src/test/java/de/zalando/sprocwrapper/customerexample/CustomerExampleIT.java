package de.zalando.sprocwrapper.customerexample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author  Jan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class CustomerExampleIT {

    @Autowired
    private CustomerExampleSProcService service;

    @Test
    public void testNothing() { }

    @Test
    public void testCreate() {
        Customer c = new Customer();
        c.setFirstName("Jan");
        c.setName("Name");

        Customer result = service.createCustomer(c);

        assertEquals(true, (int) result.getId() > 0);
        assertEquals(null, result.getDefaultAddress());
    }

    @Test
    public void testCreateWithAddress() {
        Address a = new Address();
        a.setStreet("Somestreet");

        Customer c = new Customer();
        c.setFirstName("Jan");
        c.setName("Name");
        c.setDefaultAddress(a);

        Customer result = service.createCustomer(c);
        assertEquals(1, result.getAddresses().size());
        assertEquals(true, result.getDefaultAddress() != null);
    }
}
