//J-
package de.zalando.typemapper.postgres;

import de.zalando.typemapper.AbstractTest;
import de.zalando.typemapper.namedresult.results.*;
import de.zalando.typemapper.namedresult.results.Enumeration;
import de.zalando.typemapper.namedresult.transformer.Hans;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.TestContextManager;

import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static de.zalando.typemapper.postgres.PgArray.ARRAY;
import static de.zalando.typemapper.postgres.PgRow.ROW;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(Parameterized.class)
public class PgSerializerToDatabaseTestIT extends AbstractTest {

    private TestContextManager testContextManager;

    @Override
    protected void prepareContext() throws Exception {
        testContextManager = new TestContextManager(getClass());
        testContextManager.prepareTestInstance(this);
    }

    @Before
    public void createJdbcTemplate() {
        // BasicConfigurator.configure();
        // Logger.getRootLogger().setLevel(Level.INFO);
        // Logger.getLogger(org.springframework.jdbc.datasource.DataSourceUtils.class).setLevel(Level.INFO);
        // Logger.getLogger("org.springframework.jdbc.core.JdbcTemplate").setLevel(Level.WARN);
        // Logger.getLogger("org.springframework.beans").setLevel(Level.WARN);
        // Logger.getLogger("org.springframework.jdbc.support").setLevel(Level.WARN);

        this.template = new JdbcTemplate(new SingleConnectionDataSource(this.connection, false));
    }

    // Fields
    private JdbcTemplate template;
    private final Object objectToSerialize;
    private final String expectedString;
    private final int expectedSQLType;

    /*
     * Constructor. The JUnit test runner will instantiate this class once for
     * every element in the Collection returned by the method annotated with
     *
     * @Parameters.
     */
    public PgSerializerToDatabaseTestIT(final Object objectToSerialize, final String expectedString,
            final Integer expectedSQLType) {
        this.objectToSerialize = objectToSerialize;
        this.expectedString = expectedString;
        this.expectedSQLType = expectedSQLType;
    }

    private static Map<String, String> createSimpleMap(final String key, final String val) {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(key, val);
        return map;
    }

    /*
     * Test data generator. This method is called the the JUnit parameterized
     * test runner and returns a Collection of Arrays. For each Array in the
     * Collection, each array element corresponds to a parameter in the
     * constructor.
     */
    @Parameters
    public static Collection<Object[]> generateData() throws SQLException {
        return Arrays.asList(
                new Object[][] {
                    /* 23 */
                        {PgTypeHelper.asPGobject(new InheritedClassWithPrimitivesDeprecated(1L, "1", 12)), "(1,12,1)", Types.OTHER},

                    /* 0 */
                    {1, "1", Types.INTEGER},

                    /* 1 */
                    {Integer.valueOf(69), "69", Types.INTEGER},

                    /* 2 */
                    {true, "true", Types.BOOLEAN},

                    /* 3 */
                    {ARRAY(1, 2, 3, 4).asJdbcArray("int4"), "{1,2,3,4}", Types.ARRAY},

                    /* 4 */
                    {ARRAY(null, 2, 3, 4).asJdbcArray("int4"), "{NULL,2,3,4}", Types.ARRAY},

                    /* 5 */
                    {ARRAY("a", "b").asJdbcArray("text"), "{a,b}", Types.ARRAY},

                    /* 6 */
                    {
                        ARRAY("first element", "second \"quoted\" element").asJdbcArray("text"),
                        "{\"first element\",\"second \\\"quoted\\\" element\"}", Types.ARRAY
                    },

                    /* 7 */
                    {ROW(1, 2).asPGobject("int_duplet"), "(1,2)", Types.OTHER},

                    /* 8 */
                    {
                        ROW(1, 2, ARRAY("a", "b")).asPGobject("int_duplet_with_text_array"), "(1,2,\"{a,b}\")",
                        Types.OTHER
                    },

                    /* 9 */
                    {
                        ROW("a", "b", new int[] {1, 2, 3, 4}).asPGobject("text_duplet_with_int_array"),
                        "(a,b,\"{1,2,3,4}\")", Types.OTHER
                    },

                    /* 10 */
                    {
                        ROW("a", null, ARRAY(ROW(1, 10), ROW(2, 20), null)).asPGobject(
                            "text_duplet_with_int_duplet_array"), "(a,,\"{\"\"(1,10)\"\",\"\"(2,20)\"\",NULL}\")",
                        Types.OTHER
                    },

                    /* 11 */
                    {
                        ROW("a", null, ARRAY(ROW(1, 11), ROW(2, 22), null)).asPGobject(
                            "text_duplet_with_int_duplet_array"), "(a,,\"{\"\"(1,11)\"\",\"\"(2,22)\"\",NULL}\")",
                        Types.OTHER
                    },

                    /* 12 */
                    {
                        ROW(1, new ClassWithPrimitives(1, 2L, 'c')).asPGobject("int_with_additional_type"),
                        "(1,\"(c,1,2)\")", Types.OTHER
                    },

                    /* 13 */
                    {
                        ROW(1,
                            new ClassWithPrimitives[] {
                                new ClassWithPrimitives(1, 100L, 'a'), new ClassWithPrimitives(2, 200L, 'b')
                            }).asPGobject("int_with_additional_type_array"),
                        "(1,\"{\"\"(a,1,100)\"\",\"\"(b,2,200)\"\"}\")", Types.OTHER
                    },

                    /* 14 */
                    {PgTypeHelper.asPGobject(new ClassWithPrimitives(1, 100L, 'a')), "(a,1,100)", Types.OTHER},

                    /* 15 */
                    {
                        PgTypeHelper.asPGobject(new ClassWithPrimitivesAndMap(1, 2, 'a', createSimpleMap("b", "c"))),
                        "(1,2,a,\"\"\"b\"\"=>\"\"c\"\"\")", Types.OTHER
                    },

                    /* 16 */
                    {
                        PgTypeHelper.asPGobject(new ClassWithEnum(Enumeration.VALUE_1, Enumeration.VALUE_2)),
                        "(VALUE_1,VALUE_2)", Types.OTHER
                    },

                    /* 17 */
                    {
                        PgTypeHelper.asPGobject(
                            new ClassWithSimpleTransformers(GenderCode.MALE, GenderCode.MALE, GenderCode.MALE, "path",
                                "listElement1", "listElement2", "listElement3")),
                        "(path,homme,0,MALE,listElement1#listElement2#listElement3)", Types.OTHER
                    },

                    /* 18 */
                    {
                        PgTypeHelper.asPGobject(
                            new ClassWithPredefinedTransformer(
                                new Hans("This is a complex object using an implicit transformer."))),
                        "(\"This is a complex object using an implicit transformer.\",{})", Types.OTHER
                    },

                    /* 19 */
                    {
                        PgTypeHelper.asPGobject(
                            new ClassWithPredefinedTransformer(
                                new Hans("This is a complex object using an implicit transformer."),
                                new Hans("list element 1"), new Hans("list element 2"))),
                        "(\"This is a complex object using an implicit transformer.\",\"{\"\"list element 1\"\",\"\"list element 2\"\"}\")",
                        Types.OTHER
                    },

                    /* 20 */
                    {new Date(112, 11, 1, 6, 6, 6), "2012-12-01 06:06:06.000000 +01:00:00", Types.TIMESTAMP},

                    /* 21 */
                    {new Date(112, 9, 1, 6, 6, 6), "2012-10-01 06:06:06.000000 +02:00:00", Types.TIMESTAMP},

                    /* 22 */
                    {PgTypeHelper.asPGobject(new InheritedClassWithPrimitives(1L, "1", 12)), "(1,12,1)", Types.OTHER},

                });
    }

    @Before
    public void createNeededTypes() throws SQLException {
        execute("CREATE TYPE tmp.int_duplet AS (a int, b int);");
        execute("CREATE TYPE tmp.int_duplet_with_text_array AS (a int, b int, c text[]);");
        execute("CREATE TYPE tmp.text_duplet_with_int_array AS (a text, b text, c int[]);");
        execute("CREATE TYPE tmp.text_duplet_with_int_duplet_array AS (a text, b text, c tmp.int_duplet[]);");

        // NOTE: additional_type members must be sorted alphabetically (because annotation positions were not defined)
        execute("CREATE TYPE tmp.additional_type AS (c text, i integer, l bigint);");
        execute("CREATE TYPE tmp.int_with_additional_type AS (a int, t tmp.additional_type);");
        execute("CREATE TYPE tmp.int_with_additional_type_array AS (a int, t tmp.additional_type[]);");

        // type with positional members:
        execute("CREATE TYPE tmp.additional_type_with_positions AS (i int, l bigint, c text, h hstore);");

        // transformed type:
        execute("CREATE TYPE tmp.class_with_predefined_transformer AS (a text, b text[]);");

        // type with gender code
        execute("CREATE TYPE gender_enum_type AS ENUM ('MALE', 'FEMALE');");
        execute(
            "CREATE TYPE tmp.class_with_simple_transformers AS (file_column text, gender_as_code text, gender_as_int integer, gender_as_name gender_enum_type, string_list_with_separtion_char text);");

        execute("CREATE TYPE tmp.inherited_class_with_primitives AS (l bigint, cc text, i int);");
        execute("CREATE TYPE tmp.inherited_class_with_primitives_deprecated AS (l bigint, cc text, i int);");
    }

    @After
    public void dropUsedTypes() throws SQLException {
        execute("DROP TYPE tmp.text_duplet_with_int_duplet_array;");
        execute("DROP TYPE tmp.text_duplet_with_int_array;");
        execute("DROP TYPE tmp.int_duplet_with_text_array;");
        execute("DROP TYPE tmp.int_duplet;");
        execute("DROP TYPE tmp.int_with_additional_type;");
        execute("DROP TYPE tmp.int_with_additional_type_array;");
        execute("DROP TYPE tmp.additional_type;");
        execute("DROP TYPE tmp.inherited_class_with_primitives;");
        execute("DROP TYPE tmp.inherited_class_with_primitives_deprecated;");
    }

    @Test
    public void passingParametersToQueryTest() {
        assertThat(template.queryForObject("SELECT (?)::text", new Object[] {this.objectToSerialize},
                new int[] {this.expectedSQLType}, String.class), is(this.expectedString));
    }
}
//J+
