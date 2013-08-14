package de.zalando.sprocwrapper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.sprocwrapper.example.ExampleDomainObjectWithoutSetters;
import de.zalando.sprocwrapper.example.ExampleSProcService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class ParallelExecutionIT {

    @Autowired
    private ExampleSProcService exampleSProcService;

    @Test
    public void testConcurrentAccessDomainObjectWithoutSetters() throws Exception {

        final int concurrentClients = 100;
        final int executions = 10000;

        Callable<ExampleDomainObjectWithoutSetters> call = new Callable<ExampleDomainObjectWithoutSetters>() {

            @Override
            public ExampleDomainObjectWithoutSetters call() throws Exception {
                return exampleSProcService.getEntityWithoutSetters();
            }
        };

        Collection<Callable<ExampleDomainObjectWithoutSetters>> callableCollection =
            new LinkedList<Callable<ExampleDomainObjectWithoutSetters>>();
        for (int i = 0; i < executions; i++) {
            callableCollection.add(call);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(concurrentClients);

        try {
            List<Future<ExampleDomainObjectWithoutSetters>> results = executorService.invokeAll(callableCollection);
            for (Future<ExampleDomainObjectWithoutSetters> future : results) {
                ExampleDomainObjectWithoutSetters result = future.get();

                Assert.assertNotNull(result.getA());
                Assert.assertNotNull(result.getB());
                Assert.assertNotNull(result.getC());
            }
        } finally {
            executorService.shutdown();
        }
    }

}
