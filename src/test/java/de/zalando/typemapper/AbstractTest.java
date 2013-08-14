package de.zalando.typemapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.test.context.ContextConfiguration;

import de.zalando.typemapper.core.db.DbTypeRegister;

@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public abstract class AbstractTest {

    @Value("${frontend.sproc.datasource.username}")
    private String username;
    @Value("${frontend.sproc.datasource.password}")
    private String password;
    @Value("${frontend.sproc.datasource.url}")
    private String url;

    protected Connection connection;

    public boolean execute(final String sql) throws SQLException {
        return connection.prepareStatement(sql).execute();
    }

    @Before
    public void setUp() throws Exception {
        prepareContext();

        // Get connection
        Class.forName("org.postgresql.Driver");

        final Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        connection = DriverManager.getConnection(url, props);
        execute("DROP SCHEMA IF EXISTS tmp CASCADE;");
        execute("DROP SCHEMA IF EXISTS tmp2 CASCADE;");
        execute("CREATE SCHEMA tmp;");
        execute("CREATE SCHEMA tmp2;");
        execute("set search_path to tmp,tmp2,public;");
        execute("CREATE DOMAIN tmp.gender AS char;");
        execute("CREATE TYPE tmp.type_with_domain AS (g tmp.gender)");

        execute("CREATE TYPE tmp.simple_type AS (i int, l int, c varchar);");
        execute("CREATE TYPE tmp2.simple_type AS (i int, l int, c varchar, h varchar);");
        execute("CREATE TYPE tmp.simple_type_for_embed AS (i int, l int, c varchar, str varchar);");
        execute("CREATE TYPE tmp.complex_type AS (obj tmp.simple_type, str varchar);");
        execute("CREATE TYPE tmp.array_type AS (arr tmp.simple_type[], str varchar);");
        execute("CREATE TYPE tmp.hstore_type AS (map hstore, str varchar);");
        execute("CREATE TYPE tmp.hstore_array_type AS (map_array hstore[], str varchar);");

        execute("CREATE TYPE tmp.enumeration AS ENUM ('VALUE_1', 'VALUE_2')");
        execute("CREATE TYPE tmp.simple_enumeration_type AS (a enumeration, b enumeration);");
        execute("CREATE TYPE tmp.simple_with_enumeration_type AS (a enumeration, b enumeration, str varchar);");

        execute("CREATE TABLE tmp.simple_table (i int, l int, c varchar);");
        execute("INSERT INTO tmp.simple_table (i, l, c) VALUES (1,2,'Daniel'), (2,3,'alone at'), (3,4,'home');");

        execute("CREATE TABLE tmp.test_time(lt timestamp without time zone, gt timestamp with time zone, zone text);");
        execute(
            "INSERT INTO tmp.test_time(lt, gt, zone) VALUES ('2012-07-30 16:00:00', '2012-07-30 14:00:00+0', 'utc'), ('2012-07-30 18:00:00', '2012-07-30 18:00:00+2', 'cest'), ('2012-07-30 21:00:00', '2012-07-30 10:00:00-09', 'japan');");

        final String test_time_sproc =
            "CREATE OR REPLACE FUNCTION tmp.test_time_function(OUT id smallint, OUT msg text) " + "RETURNS record AS "
                + "$BODY$ " + "DECLARE " + "BEGIN " + "id  := 0; " + "msg := 'result_code'; " + "RETURN; " + "END "
                + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        final String primitive_sproc =
            "CREATE OR REPLACE FUNCTION tmp.primitives_function(OUT id smallint, OUT msg text) " + "RETURNS record AS "
                + "$BODY$ " + "DECLARE " + "BEGIN " + "id  := 0; " + "msg := 'result_code'; " + "RETURN; " + "END "
                + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        execute(primitive_sproc);

        final String sproc_with_null_result =
            "CREATE OR REPLACE FUNCTION tmp.primitives_with_null_function(OUT id smallint, OUT msg text) "
                + "RETURNS record AS " + "$BODY$ " + "DECLARE " + "BEGIN " + "id  := 0; " + "RETURN; " + "END "
                + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";
        execute(sproc_with_null_result);

        final String primitive_sproc_2 =
            "CREATE OR REPLACE FUNCTION tmp2.primitives_function(OUT id smallint, OUT msg text, OUT msg2 text) "
                + "RETURNS record AS " + "$BODY$ " + "DECLARE " + "BEGIN " + "id  := 0; " + "msg := 'result_code'; "
                + "msg2 := 'result_code_2'; " + "RETURN; " + "END " + " $BODY$ "
                + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";
        execute(primitive_sproc_2);

        final String array_sproc =
            "CREATE OR REPLACE FUNCTION tmp.array_function(OUT id smallint, OUT msg text, OUT movies tmp.simple_type[]) "
                + "RETURNS record AS " + "$BODY$ " + "DECLARE " + "BEGIN " + "id  := 0; " + "msg := 'result_code'; "
                + "movies := ARRAY(SELECT ROW(i,l,c)::tmp.simple_type FROM tmp.simple_table ORDER BY i); " + "RETURN; "
                + "END " + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        execute(array_sproc);

        final String string_array_sproc =
            "CREATE OR REPLACE FUNCTION tmp.string_array_function(OUT str text, OUT arr text[]) " + "RETURNS record AS "
                + "$BODY$ " + "DECLARE " + "BEGIN " + "str := 'str'; " + "arr := ARRAY['result_1','result_2']; "
                + "RETURN; " + "END " + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        execute(string_array_sproc);

        final String string_null_array_sproc =
            "CREATE OR REPLACE FUNCTION tmp.string_null_array_function(OUT str text, OUT arr text[]) "
                + "RETURNS record AS " + "$BODY$ " + "DECLARE " + "BEGIN " + "str := 'str'; " + "RETURN; " + "END "
                + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        execute(string_null_array_sproc);

        final String hstore_type_sproc =
            "CREATE OR REPLACE FUNCTION tmp.hstore_type_function(OUT aa tmp.hstore_type, OUT bb text) "
                + "RETURNS record AS " + "$BODY$ " + "DECLARE " + "BEGIN "
                + "aa := CAST (ROW( hstore('key','val'), 'str') AS tmp.hstore_type); " + "bb := 'temp';" + "RETURN ; "
                + "END " + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        execute(hstore_type_sproc);

        final String hstore_array_type_sproc =
            "CREATE OR REPLACE FUNCTION tmp.hstore_array_type_function(OUT aa tmp.hstore_array_type, OUT bb text) "
                + "RETURNS record AS " + "$BODY$ " + "DECLARE " + "BEGIN "
                + "aa := CAST ( ROW( ARRAY[ hstore('key','val') ], 'str') AS tmp.hstore_array_type); " + "bb := 'temp';"
                + "RETURN ; " + "END " + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        execute(hstore_array_type_sproc);

        final String simple_with_enumeration_type_function =
            "CREATE OR REPLACE FUNCTION tmp.simple_with_enumeration_type_function(OUT aa tmp.simple_with_enumeration_type) "
                + "RETURNS tmp.simple_with_enumeration_type AS " + "$BODY$ " + "DECLARE " + "BEGIN "
                + "aa := CAST ( ROW( 'VALUE_1'::tmp.enumeration, 'VALUE_2'::tmp.enumeration, 'str') AS tmp.simple_with_enumeration_type); "
                + "RETURN ; " + "END " + " $BODY$ " + " LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER ";

        execute(simple_with_enumeration_type_function);

        // reload the register for this test
        DbTypeRegister.reInitRegister(connection);
    }

    protected void prepareContext() throws Exception { }

    @After
    public void tearDown() throws SQLException {
        execute("DROP SCHEMA IF EXISTS tmp CASCADE;");
        execute("DROP SCHEMA IF EXISTS tmp2 CASCADE;");

        if (connection != null) {
            connection.close();
        }
    }

}
