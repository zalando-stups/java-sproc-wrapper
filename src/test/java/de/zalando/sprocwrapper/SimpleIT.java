package de.zalando.sprocwrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import javax.validation.ConstraintViolationException;

import org.joda.time.DateTime;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.zalando.sprocwrapper.example.AddressPojo;
import de.zalando.sprocwrapper.example.Example1DomainObject1;
import de.zalando.sprocwrapper.example.Example1DomainObject2;
import de.zalando.sprocwrapper.example.Example2DomainObject1;
import de.zalando.sprocwrapper.example.Example2DomainObject2;
import de.zalando.sprocwrapper.example.ExampleDomainObject;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithDate;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithEmbed;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithEnum;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithGlobalTransformer;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithInnerObject;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithMap;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithRandomFields;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithRandomFieldsInner;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithRandomFieldsOverride;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithSimpleTransformer;
import de.zalando.sprocwrapper.example.ExampleDomainObjectWithValidation;
import de.zalando.sprocwrapper.example.ExampleEnum;
import de.zalando.sprocwrapper.example.ExampleNamespacedSProcService;
import de.zalando.sprocwrapper.example.ExampleSProcService;
import de.zalando.sprocwrapper.example.ExampleValidationSProcService;
import de.zalando.sprocwrapper.example.GlobalTransformedObject;
import de.zalando.sprocwrapper.example.LookupType;
import de.zalando.sprocwrapper.example.LookupTypeSchema;
import de.zalando.sprocwrapper.example.TestInheritanceChild;
import de.zalando.sprocwrapper.example.WrapperLookup;
import de.zalando.sprocwrapper.example.WrapperLookupSchema;

import de.zalando.typemapper.parser.DateTimeUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class SimpleIT {

    @Autowired
    private ExampleSProcService exampleSProcService;

    @Autowired
    private ExampleValidationSProcService exampleValidationSProcService;

    @Autowired
    private ExampleNamespacedSProcService exampleNamespacedSProcService;

    @Autowired
    @Qualifier("testDataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("testDataSource1")
    private DataSource dataSource1;

    @Test
    public void testSample() throws SQLException {

        // test void result
        exampleSProcService.getSimpleIntVoid(1);

        assertEquals(3, (int) exampleSProcService.getSimpleInt());
        assertEquals(3, exampleSProcService.getSimpleIntAsPrimitive());
        exampleSProcService.createArticleSimpleItems("sku", 1, 12, 13, "1001");

        assertEquals(true, exampleSProcService.getBoolean());

        exampleSProcService.setBoolean(true);
    }

    @Test
    public void testSimpleTransformer() throws SQLException {

        // test complex result
        final ExampleDomainObjectWithSimpleTransformer transformed = exampleSProcService.testSimpleTransformer(
                new ExampleDomainObjectWithSimpleTransformer("123", "hallo"));

        assertEquals("123", transformed.getA());
        assertEquals("hallo", transformed.getB());
    }

    @Test
    public void testMicroSecondTimestamp() throws SQLException {
        java.sql.Timestamp t = exampleSProcService.getMicorsecondTimestamp();
        assertEquals(t.getNanos(), 123456000);
        System.out.println(t);
    }

    @Test
    public void testGlobalTransformer() throws SQLException {

        // test void result
        final ExampleDomainObjectWithGlobalTransformer transformed = exampleSProcService.testGlobalTransformer(
                new ExampleDomainObjectWithGlobalTransformer("123", new GlobalTransformedObject("hallo"),
                    Lists.newArrayList(new GlobalTransformedObject("list element 1"),
                        new GlobalTransformedObject("list element 2")),
                    Sets.newHashSet(new GlobalTransformedObject("set element 1"),
                        new GlobalTransformedObject("set element 2"))));

        assertEquals("123", transformed.getA());
        assertEquals("hallo", transformed.getB().getValue());
        Assert.assertTrue(transformed.getC().contains(new GlobalTransformedObject("list element 1")));
        Assert.assertTrue(transformed.getC().contains(new GlobalTransformedObject("list element 2")));
        Assert.assertTrue(transformed.getD().contains(new GlobalTransformedObject("set element 1")));
        Assert.assertTrue(transformed.getD().contains(new GlobalTransformedObject("set element 2")));
    }

    @Test
    public void testGlobalTransformer2() throws SQLException {

        // test void result
        final GlobalTransformedObject transformed = exampleSProcService.testGlobalTransformer2(
                new GlobalTransformedObject("global transformed value"));

        assertEquals("global transformed value", transformed.getValue());
    }

    @Test
    public void testGlobalTransformer3() throws SQLException {

        // test void result
        final GlobalTransformedObject transformed = exampleSProcService.testGlobalTransformer3(
                new GlobalTransformedObject("global transformed value"), new ExampleDomainObject("EDO a", "EDO b"));

        assertEquals("global transformed value:b:EDO aEDO b", transformed.getValue());
    }

    @Test
    public void testGlobalTransformer4() throws SQLException {

        // test void result
        final List<GlobalTransformedObject> transformed = exampleSProcService.testGlobalTransformer4(Lists.newArrayList(
                    new GlobalTransformedObject("global transformed value 1"),
                    new GlobalTransformedObject("global transformed value 2")),
                new ExampleDomainObject("EDO a", "EDO b"));

        assertNotNull(transformed);
        assertEquals(2, transformed.size());
        assertEquals("global transformed value 1", transformed.get(0).getValue());
        assertEquals("global transformed value 2", transformed.get(1).getValue());
    }

    @Test
    public void testGlobalTransformer5() throws SQLException {

        // test void result
        final GlobalTransformedObject transformed = exampleSProcService.testGlobalTransformer2(null);
        assertNull(transformed);
    }

    @Test
    public void testGlobalTransformer6() throws SQLException {

        // test void result
        final GlobalTransformedObject transformed = exampleSProcService.testGlobalTransformer2(
                new GlobalTransformedObject(null));

        // we cannot distinct on sql-level if the null is GlobalTransformedObject of GlobalTransformedObject.value
        assertNull(transformed);
    }

    @Test
    public void testGlobalTransformer7() throws SQLException {

        // test void result
        final List<GlobalTransformedObject> transformed = exampleSProcService.testGlobalTransformer5(Sets.newHashSet(
                    new GlobalTransformedObject(null)), null);

        // we cannot distinct on sql-level if the null is GlobalTransformedObject of GlobalTransformedObject.value
        assertNotNull(transformed);
        assertEquals(1, transformed.size());
        assertNull(transformed.get(0).getValue());
    }

    @Test
    public void testGlobalTransformer8() throws SQLException {

        // test void result
        final DateTime dateTime = new DateTime();
        final DateTime transformed = exampleSProcService.testGlobalTransformer6(dateTime);

        // we cannot distinct on sql-level if the null is GlobalTransformedObject of GlobalTransformedObject.value
        assertNotNull(transformed);
        assertEquals(dateTime, transformed);
    }

    @Test
    public void testGlobalTransformer9() throws SQLException {
        final DateTime dateTime = new DateTime(2013, 2, 20, 18, 20, 0, 0);
        final DateTime transformed = exampleSProcService.testGlobalTransformer6(dateTime);

        assertNotNull(transformed);
        assertEquals(dateTime, transformed);
    }

    @Test
    public void testGlobalTransformer10() throws SQLException {
        final DateTime dateTime = new DateTime(2013, 2, 20, 18, 20, 0, 10);
        final DateTime transformed = exampleSProcService.testGlobalTransformer6(dateTime);

        assertNotNull(transformed);
        assertEquals(dateTime, transformed);
    }

    @Test
    public void testGlobalTransformer11() throws SQLException {
        final DateTime dateTime = new DateTime(2013, 2, 20, 18, 20, 0, 100);
        final DateTime transformed = exampleSProcService.testGlobalTransformer6(dateTime);

        assertNotNull(transformed);
        assertEquals(dateTime, transformed);
    }

    @Test
    public void testGlobalTransformer12() throws SQLException {
        final DateTime dateTime = new DateTime(2013, 2, 20, 18, 20, 0, 111);
        final DateTime transformed = exampleSProcService.testGlobalTransformer6(dateTime);

        assertNotNull(transformed);
        assertEquals(dateTime, transformed);
    }

    @Test
    public void testSimpleListParam() throws SQLException {

        final List<String> skus = new ArrayList<String>();
        skus.add("ABC123");
        skus.add("ABC456");

        exampleSProcService.createArticleSimples(skus);
    }

    @Test
    public void testMultiRowTypeMappedResult() {

        // Query for a Multi Row Resultset of TestResult Objects
        final List<ExampleDomainObject> rows = exampleSProcService.getResult();
        assertEquals("a", rows.get(0).getA());
        assertEquals("b", rows.get(0).getB());
        assertEquals("c", rows.get(1).getA());
        assertEquals("d", rows.get(1).getB());
    }

    @Test
    public void testParameterOverloading() {
        assertEquals(3, (int) exampleSProcService.getSimpleInt());
        assertEquals(1234, exampleSProcService.getSimpleInt(1234));
    }

    @Test
    public void testObjectParam() {

        String result = exampleSProcService.createOrUpdateObject(null);
        assertEquals(null, result);

        final ExampleDomainObject obj = new ExampleDomainObject("a", "b");
        result = exampleSProcService.createOrUpdateObject(obj);
        assertEquals("a b", result);
    }

    @Test
    public void testListParam() {

        String result = exampleSProcService.createOrUpdateMultipleObjects(null);
        assertEquals("", result);

        result = exampleSProcService.createOrUpdateMultipleObjects(new ArrayList<ExampleDomainObject>());
        assertEquals("", result);

        final ExampleDomainObject obj = new ExampleDomainObject("a", "b");
        final List<ExampleDomainObject> list = new ArrayList<ExampleDomainObject>();
        list.add(obj);
        list.add(new ExampleDomainObject("c", "d"));

        result = exampleSProcService.createOrUpdateMultipleObjects(list);
        assertEquals("a_b,c_d,", result);
    }

    @Test
    public void testListParamWithMap() {

        String result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(null);
        assertNull(result);

        result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(new ArrayList<ExampleDomainObjectWithMap>());
        assertNull(result);

        final ExampleDomainObjectWithMap obj = new ExampleDomainObjectWithMap("a", null);
        final List<ExampleDomainObjectWithMap> list = new ArrayList<ExampleDomainObjectWithMap>();
        list.add(obj);
        list.add(new ExampleDomainObjectWithMap("c", new HashMap<String, String>()));
        list.get(1).b.put("key", "val");

        result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(list);
        assertEquals("<c_key_val>", result);

        list.get(0).b = new HashMap<String, String>();

        result = exampleSProcService.createOrUpdateMultipleObjectsWithMap(list);
        assertEquals("<a__>,<c_key_val>", result);

        // test void result
        exampleSProcService.createOrUpdateMultipleObjectsWithMapVoid(list);
    }

    @Test
    public void textComplexParam() {

        String result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(null);
        assertNull(result);

        result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(
                new ArrayList<ExampleDomainObjectWithInnerObject>());
        assertNull(result);

        final ExampleDomainObjectWithInnerObject obj = new ExampleDomainObjectWithInnerObject("a", null);
        final List<ExampleDomainObjectWithInnerObject> list = new ArrayList<ExampleDomainObjectWithInnerObject>();
        list.add(obj);
        list.add(new ExampleDomainObjectWithInnerObject("c", new ExampleDomainObject("d", "e")));

        result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(list);
        assertEquals("<c_d|e>", result);

        obj.setC(new ArrayList<ExampleDomainObject>());
        obj.getC().add(new ExampleDomainObject("f", "g"));
        result = exampleSProcService.createOrUpdateMultipleObjectsWithInnerObject(list);
        assertEquals("<c_d|e>", result);
    }

    @Test
    public void testEnum() {
        exampleSProcService.useEnumParam(ExampleEnum.ENUM_CONST_1);

        // exampleSProcService.createOrUpdateObjectWithEnum(null);

        final ExampleDomainObjectWithEnum obj = new ExampleDomainObjectWithEnum();
        obj.setX("X");
        obj.setMyEnum(ExampleEnum.ENUM_CONST_1);

        final String result = exampleSProcService.createOrUpdateObjectWithEnum(obj);
        assertEquals("XENUM_CONST_1", result);
    }

    @Test
    public void testReturnDomainObjectWithEnum() {
        final ExampleDomainObjectWithEnum obj = exampleSProcService.getEntityWithEnum(1L);
        Assert.assertNotNull(obj);
        Assert.assertEquals("sample x", obj.getX());
        Assert.assertEquals(ExampleEnum.ENUM_CONST_1, obj.getMyEnum());
    }

    @Test
    public void testDate() {
        exampleSProcService.useDateParam(null);
        exampleSProcService.useDateParam(new Date(System.currentTimeMillis()));

        // commented out, because date input parameters are not working at the moment
        // exampleSProcService.useDateParam2(new Date(System.currentTimeMillis()));

        final ExampleDomainObjectWithDate obj = new ExampleDomainObjectWithDate();
        obj.setX("X");

        String result = exampleSProcService.createOrUpdateObjectWithDate(obj);
        assertNull(result);

        final Date d = new Date(System.currentTimeMillis());
        obj.setMyDate(d);
        result = exampleSProcService.createOrUpdateObjectWithDate(obj);
        assertEquals("X" + (new SimpleDateFormat("yyyy-MM-dd").format(d)), result);

        final Timestamp t = new Timestamp(System.currentTimeMillis());
        t.setNanos(123456789);
        obj.setMyTimestamp(t);
        result = exampleSProcService.createOrUpdateObjectWithDate(obj);
        assertEquals("X" + (new SimpleDateFormat("yyyy-MM-dd").format(d)) + DateTimeUtil.format(t), result);
    }

    @Test
    public void testChar() {
        exampleSProcService.useCharParam('m');
    }

    @Test
    public void testReturnDate() {
        final Date d = exampleSProcService.getFixedTestDate();
        System.out.println("Date d:" + d);
        System.out.println("Date now:" + (new Date(System.currentTimeMillis())));

        System.out.println(d.getClass().getName());

        assertEquals(1328266821000L, d.getTime()); // extract(epoch from '2012-02-03 12:00:21'::timestamp)*1000
    }

    @Test
    public void testIntegerListParam() {
        exampleSProcService.useIntegerListParam(Lists.newArrayList(1, 2));
    }

    @Test
    public void testCreateAddress() {
        final AddressPojo a = getNewTestAddress();

        final AddressPojo b = exampleSProcService.createAddress(a);
        assertNotNull(b);
        assertNotNull(b.id);

        final AddressPojo c = exampleSProcService.createAddress(a);
        assertNotNull(c);
        assertNotNull(c.id);
    }

    @Test
    public void testGetAddress() {
        final AddressPojo a = getNewTestAddress();

        final AddressPojo b = exampleSProcService.createAddress(a);

        final AddressPojo c = new AddressPojo();
        c.id = b.getId();

        final AddressPojo l = exampleSProcService.getAddress(c);
        System.out.println(l);

        assertEquals(l.customerId, a.customerId);
        assertEquals(l.number, a.number);
        assertEquals(l.street, a.street);

        System.out.println(l);
    }

    private static int addresscount = 1;

    private AddressPojo getNewTestAddress() {
        final AddressPojo a = new AddressPojo();
        a.customerId = addresscount++;
        a.street = "Auf Beverau";
        a.number = "11";
        return a;
    }

    @Test
    public void testGetAddressSql() {

        final AddressPojo a = getNewTestAddress();

        final AddressPojo b = exampleSProcService.createAddress(a);

        final AddressPojo c = new AddressPojo();
        c.id = b.getId();

        final AddressPojo l = exampleSProcService.getAddress(c);
        System.out.println(l);

        assertEquals(l.customerId, a.customerId);
        assertEquals(l.number, a.number);
        assertEquals(l.street, a.street);

        System.out.println(l);
    }

    @Test
    public void testSensitiveParameter() {

        // password should not be logged to logfile!
        exampleSProcService.login("henning.jacobs", "mySecR3tPassW0rd");
    }

    @Test
    public void testNamespacedService() {
        assertEquals("TESTRESULT", exampleNamespacedSProcService.test());
    }

    @Test
    public void testPrimitiveListResults() {
        final List<Integer> ints = exampleSProcService.getInts();
        assertEquals(2, ints.size());
        assertEquals(1, (int) ints.get(0));
        assertEquals(2, (int) ints.get(1));

        final List<Long> longs = exampleSProcService.getLongs();
        assertEquals(2, longs.size());
        assertEquals(1000, (long) longs.get(0));
        assertEquals(2002, (long) longs.get(1));
    }

    /**
     * test correct mapping of complex types with inner type and random field ordering (i.e. not alphabetically sorted)
     */
    @Test
    public void textComplexParamNameMapping() {

        String result = exampleSProcService.createOrUpdateObjectWithRandomFields(null);
        assertNull(result);

        final ExampleDomainObjectWithRandomFields obj = new ExampleDomainObjectWithRandomFields();
        obj.setX("X");
        obj.setY("Y");
        obj.setZ(3);
        obj.setInnerObject(new ExampleDomainObjectWithRandomFieldsInner("x", "y", "z"));
        obj.setList(Lists.newArrayList(new ExampleDomainObjectWithRandomFieldsInner("a", "b", "c")));
        result = exampleSProcService.createOrUpdateObjectWithRandomFields(obj);

        // check that field ordering is correct
        assertEquals("XY3xyz(<abc>)", result);

        result = exampleSProcService.createOrUpdateMultipleObjectsWithRandomFields(Lists.newArrayList(
                    new ExampleDomainObjectWithRandomFields("X", "Y", 1)));
        assertEquals("XY1", result);
    }

    /**
     * test correct mapping of complex types with inner type and random field ordering (i.e. not alphabetically sorted)
     */
    @Test
    public void textComplexParamNameMappingNoAnnotation() {

        String result;

        result = exampleSProcService.createOrUpdateMultipleObjectsWithRandomFieldsNoAnnotation(Lists.newArrayList(
                    new ExampleDomainObjectWithRandomFields("X", "Y", 1)));
        assertEquals("XY1", result);
    }

    /**
     * Test override of database type in domain objects passed in lists.
     */
    @Test
    public void textComplexParamNameMappingNoAnnotationOverride() {

        String result;

        result = exampleSProcService.createOrUpdateMultipleObjectsWithRandomFieldsNoAnnotationOverride(Lists
                    .newArrayList(new ExampleDomainObjectWithRandomFieldsOverride("X", "Y", 1)));
        assertEquals("XY1", result);
    }

    @Test
    @Ignore("performance test only")
    public void testRuntime() {
        assertEquals(1, 1);

        final int loops = 10000;

        final String sql = "SELECT ";

        final int xx = (new JdbcTemplate(dataSource1)).queryForInt(sql + 11111);

        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            final int j = (new JdbcTemplate(dataSource1)).queryForInt(sql + i);
        }

        final long endTime = System.currentTimeMillis();

        final long startTimeW = System.currentTimeMillis();
        for (int i = 0; i < loops; i++) {
            final int j = exampleSProcService.getSimpleInt(i);
        }

        final long endTimeW = System.currentTimeMillis();

        final long startTimeN = System.currentTimeMillis();

        for (int i = 0; i < loops; i++) {
            Connection conn = null;
            try {
                conn = dataSource1.getConnection();

                final Statement st = conn.createStatement();

                int j = 0;

                final ResultSet rs = st.executeQuery("SELECT " + i);

                if (rs.next()) {
                    j = rs.getInt(1);
                }

            } catch (final SQLException e) { }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (final SQLException e) { }
                }
            }
        }

        final long endTimeN = System.currentTimeMillis();

        System.out.println("Time used for native JdbcTemplate: " + (endTime - startTime));
        System.out.println("Time used for SprocWrapper: " + (endTimeW - startTimeW));
        System.out.println("Time used for Native: " + (endTimeN - startTimeN));
    }

    @Test
    public void testTimeout() {
        final String timeout = exampleSProcService.showTimeout();

        exampleSProcService.testTimeoutSetTo3s(2);

        try {
            exampleSProcService.testTimeoutSetTo3s(4);
            assertEquals(true, false);
        } catch (final Exception e) {
            assertEquals(true, true);
        }

        final String timeout2 = exampleSProcService.showTimeout();
        assertEquals(timeout, timeout2);

        try {
            exampleSProcService.testTimeoutSetTo5s(6);
            assertEquals(true, false);
        } catch (final Exception e) {
            assertEquals(true, true);
        }

        final String timeout3 = exampleSProcService.showTimeout();
        assertEquals(timeout, timeout3);
    }

    @Test
    public void testNullObject() {
        final ExampleDomainObjectWithInnerObject obj = exampleSProcService.getObjectWithNull();

        assertEquals(null, obj.getC());
    }

    @Test
    public void testValidValidation1() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 4);

        exampleSProcService.testSprocCallWithoutValidation1(obj);
        exampleSProcService.testSprocCallWithoutValidation2(obj);
        exampleSProcService.testSprocCallWithValidation(obj);

        exampleValidationSProcService.testSprocCallWithoutValidation(obj);
        exampleValidationSProcService.testSprocCallWithValidation1(obj);
        exampleValidationSProcService.testSprocCallWithValidation2(obj);
        exampleValidationSProcService.testSprocCallWithValidation3(obj);
    }

    @Test
    public void testValidValidation2() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 1);
        exampleSProcService.testSprocCallWithoutValidation1(obj);
        exampleSProcService.testSprocCallWithoutValidation2(obj);
        exampleValidationSProcService.testSprocCallWithoutValidation(obj);
    }

    @Test
    public void testValidValidation3() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation(null, null);
        exampleSProcService.testSprocCallWithoutValidation1(obj);
        exampleSProcService.testSprocCallWithoutValidation2(obj);
        exampleValidationSProcService.testSprocCallWithoutValidation(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation4() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 1);
        exampleSProcService.testSprocCallWithValidation(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation5() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 1);
        exampleValidationSProcService.testSprocCallWithValidation1(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation6() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 1);
        exampleValidationSProcService.testSprocCallWithValidation2(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation7() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 1);
        exampleValidationSProcService.testSprocCallWithValidation3(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation8() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation(null, null);
        exampleSProcService.testSprocCallWithValidation(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation9() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation(null, null);
        exampleValidationSProcService.testSprocCallWithValidation1(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation10() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation(null, null);
        exampleValidationSProcService.testSprocCallWithValidation2(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidation11() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation(null, null);
        exampleValidationSProcService.testSprocCallWithValidation3(obj);
    }

    @Test
    public void testValidValidationReturnValue1() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 4);
        exampleSProcService.testSprocCallWithValidationInvalidRet1(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidValidationReturnValue2() {
        final ExampleDomainObjectWithValidation obj = new ExampleDomainObjectWithValidation("test", 4);
        exampleSProcService.testSprocCallWithValidationInvalidRet2(obj);
    }

    @Test
    public void testReturnDomainObjectWithEmbed() {
        ExampleDomainObjectWithEmbed result = exampleSProcService.getEntityWithEmbed();
        assertNotNull(result);
        assertEquals("x", result.getX());

        ExampleDomainObject y = result.getY();
        assertNotNull(y);
        assertEquals("a", y.getA());
        assertEquals("b", y.getB());
    }

    @Test
    public void testReturnDomainObjectWithEmbedEmptyString() {
        ExampleDomainObjectWithEmbed result = exampleSProcService.getEntityWithEmbedEmptyString();
        assertNotNull(result);
        assertEquals("x", result.getX());

        ExampleDomainObject y = result.getY();
        assertNotNull(y);
        assertNull(y.getA());
        assertEquals("", y.getB());
    }

    @Test
    public void testReturnDomainObjectWithEmbedNullFields() {
        ExampleDomainObjectWithEmbed result = exampleSProcService.getEntityWithEmbedNullFields();
        assertNotNull(result);
        assertEquals("x", result.getX());

        ExampleDomainObject y = result.getY();
        assertNotNull(y);
        assertNull(y.getA());
        assertNull(y.getB());
    }

    @Test
    public void testResourcesWithNumbers1() {
        Example1DomainObject1 input = new Example1DomainObject1();
        input.setExample1Field1("example1field1");

        Example1DomainObject2 input2 = new Example1DomainObject2();
        input2.setExample1Field1("example1complexfield1");
        input2.setExample1Field2("example1complexfield2");
        input.setExample1Field2(input2);

        Example1DomainObject1 output = exampleSProcService.getExample1EntityWithNumbers1(input);
        assertNotNull(output);
        assertEquals("example1field1", output.getExample1Field1());

        Example1DomainObject2 object2 = output.getExample1Field2();
        assertNotNull(object2);
        assertEquals("example1complexfield1", object2.getExample1Field1());
        assertEquals("example1complexfield2", object2.getExample1Field2());
    }

    @Test
    public void testResourcesWithNumbers2() {

        Example2DomainObject1 output = exampleSProcService.getExample2EntityWithNumbers1();
        assertNotNull(output);
        assertEquals("example2field1", output.getExample2Field1());

        Example2DomainObject2 object2 = output.getExample2Field2();
        assertNotNull(object2);
        assertEquals("example2complexfield1", object2.getExample2Field1());
        assertEquals("example2complexfield2", object2.getExample2Field2());
    }

    @Test
    public void testReturnDomainObjectWithNullInnerObject() {
        ExampleDomainObjectWithInnerObject result = exampleSProcService.getEntityWithNullInnerObject();
        assertNotNull(result);

        assertEquals("a", result.getA());

        ExampleDomainObject b = result.getB();
        assertNotNull(b);
        assertNull(b.getA());
        assertNull(b.getB());

        assertNull(result.getC());
    }

    @Ignore
    @Test
    public void testEnumReturnValueU() {
        ExampleEnum e = exampleSProcService.getExampleEnum();
        assertEquals(e, ExampleEnum.ENUM_CONST_2);
    }

    @Test
    public void testTypeLookupBug() {
        LookupType t = exampleSProcService.getValueForTypeLookup();
        assertNotNull(t);
        assertEquals(1, t.a);
        assertEquals(2, t.b);
    }

    @Test
    public void testTypeLookupBugWithList() {
        List<LookupType> t = exampleSProcService.getValueForTypeLookupList();
        assertNotNull(t);
        assertEquals(1, t.size());

        LookupType entry = t.get(0);
        assertEquals(1, entry.a);
        assertEquals(2, entry.b);
    }

    @Test
    public void testTypeLookupBugWithInnerList() throws Exception {
        WrapperLookup t = exampleSProcService.getValueForTypeLookupInnerList();
        assertNotNull(t);
        assertEquals(1, t.count);

        List<LookupType> bugs = t.bugs;
        assertNotNull(bugs);
        assertEquals(1, bugs.size());

        LookupType entry = bugs.get(0);
        assertEquals(1, entry.a);
        assertEquals(2, entry.b);
    }

    @Test
    @Ignore
    public void testTypeLookupBugWithSchema() throws Exception {
        WrapperLookupSchema t = exampleSProcService.getValueForTypeLookupSchema();
        assertNotNull(t);
        assertEquals(1, t.count);

        List<LookupTypeSchema> bugs = t.bugs;
        assertNotNull(bugs);
        assertEquals(1, bugs.size());

        LookupTypeSchema entry = bugs.get(0);
        assertEquals(1, entry.a);
        assertEquals(2, entry.b);
    }

    @Test
    public void testInheritanceInParameter() throws Exception {
        TestInheritanceChild child = new TestInheritanceChild(1, 5, 7);
        int result = exampleSProcService.testInheritanceFunction(child);
        assertEquals(13, result);
    }
}
