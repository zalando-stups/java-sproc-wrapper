package de.zalando.sprocwrapper;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.sprocwrapper.example.ExampleSProcService;
import de.zalando.sprocwrapper.example.NotPartialObject;
import de.zalando.sprocwrapper.example.PartialObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class PartialDatabaseTypeIT {

    @Autowired
    private ExampleSProcService exampleSProcService;

    @Test
    public void testPartialObject() {
        PartialObject partialObject = new PartialObject();
        partialObject.setId(999);
        partialObject.setName("Test Name");

        PartialObject partialObjectDb = exampleSProcService.getPartialObject(partialObject);

        Assert.assertEquals(0, partialObjectDb.getId());
        Assert.assertEquals("Test Name", partialObjectDb.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotPartialObject() {
        exampleSProcService.getPartialObject(new NotPartialObject());
    }

}
