package de.zalando.typemapper.postgres;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.zalando.typemapper.parser.exception.HStoreParseException;

public class HStoreTest {

    static final String[] validHStoreStrings = {
        "a=>1", "a=>1, b=>2", "a=>\"two words\"", "\"strange key\" => \"\\\"quoted\\\" value\"",
        "\"strange key\" => \"\\\"quoted\\\" value\",     \"strange key2\" => \"еще один \\\"quoted\\\" value\""
    };

    @Before
    public void setUp() throws Exception { }

    @Test
    public void testHStore() throws HStoreParseException {
        for (String s : validHStoreStrings) {
            HStore hs = new HStore(s);
            for (Map.Entry<String, String> e : hs) {
                assertNotNull(e);
            }
        }
    }

    @Test
    public void testSimpleMap() {
        HStore hs;
        hs = new HStore("a=>b");

        Map<String, String> m = hs.asMap();
        assertNotNull(m);
        assertTrue(m.containsKey("a"));
        assertThat(m.get("a"), is("b"));
    }

    @Test
    public void testSimpleMultiMap() {
        HStore hs;
        hs = new HStore("a=>b, c=>d, e=>f");

        Map<String, String> m = hs.asMap();
        assertNotNull(m);
        assertTrue(m.containsKey("a"));
        assertThat(m.get("a"), is("b"));
        assertTrue(m.containsKey("c"));
        assertThat(m.get("c"), is("d"));
        assertTrue(m.containsKey("e"));
        assertThat(m.get("e"), is("f"));
    }

    @Test
    public void testQuotedMap() {
        HStore hs;
        hs = new HStore("a=>\"two words\"");

        Map<String, String> m = hs.asMap();
        assertNotNull(m);
        assertTrue(m.containsKey("a"));
        assertThat(m.get("a"), is("two words"));
    }

    @Test
    public void testQuotedMultiMap() {
        HStore hs;
        hs = new HStore(
                "a=>\"two words\", \"Complex B\"=>\"Three word value\", \"Quoted \\\"Key\\\"\"=>\"Three \\\"word\\\" value\"");

        Map<String, String> m = hs.asMap();
        assertNotNull(m);
        assertTrue(m.containsKey("a"));
        assertThat(m.get("a"), is("two words"));
        assertTrue(m.containsKey("Complex B"));
        assertThat(m.get("Complex B"), is("Three word value"));
        assertTrue(m.containsKey("Quoted \"Key\""));
        assertThat(m.get("Quoted \"Key\""), is("Three \"word\" value"));
    }

    @Test
    public void testInvalidRawValue() {
        HStore hs;
        hs = new HStore(
                "a=>\"two words\", \"Complex B\"=>\"Three word value\", \"Quoted \\\"Key\\\"\"=>\"Three \\\"word\\\" value\"");

        Map<String, String> m = hs.asMap();
        assertNotNull(m);
        assertTrue(m.containsKey("a"));
        assertThat(m.get("a"), is("two words"));
        assertTrue(m.containsKey("Complex B"));
        assertThat(m.get("Complex B"), is("Three word value"));
        assertTrue(m.containsKey("Quoted \"Key\""));
        assertThat(m.get("Quoted \"Key\""), is("Three \"word\" value"));
    }

    @Test
    public void testNullValue() {
        HStore hs;
        hs = new HStore("a=>\"NULL\", b=>NULL, null=>\"Null value\"");

        Map<String, String> m = hs.asMap();
        assertNotNull(m);
        assertTrue(m.containsKey("a"));
        assertThat(m.get("a"), is("NULL"));
        assertTrue(m.containsKey("b"));
        assertThat(m.get("b"), nullValue());
        assertTrue(m.containsKey("null"));
        assertThat(m.get("null"), is("Null value"));
    }

    @Test
    public void testQuotedWithComma() {
        HStore hs;
        String str = "\"de\"=>\"XX [00\\\" - 01\\\"]\", \"en\"=>\"DONT TOUCH - ZZ TEST [12\\\" - 42\\\"]\"";

        hs = new HStore(str);

        Map<String, String> m = hs.asMap();
        assertNotNull(m);
        assertTrue(m.containsKey("de"));
        assertTrue(m.containsKey("en"));
    }
}
