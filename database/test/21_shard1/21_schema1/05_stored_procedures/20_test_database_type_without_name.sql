CREATE OR REPLACE FUNCTION test_database_type_without_name(r ztest_schema1.lookup_type[])
   RETURNS SETOF ztest_schema1.lookup_type AS
$BODY$
DECLARE
    t ztest_schema1.lookup_type;
BEGIN
    FOR i IN array_lower(r, 1) .. array_upper(r, 1)
    LOOP
        t = r[i];
        RETURN NEXT t;
    END LOOP;
END;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;