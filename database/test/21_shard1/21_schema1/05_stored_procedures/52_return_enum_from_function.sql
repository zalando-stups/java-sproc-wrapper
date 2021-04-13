CREATE OR REPLACE FUNCTION ztest_schema1.return_enum_from_function() RETURNS example_enum AS
$BODY$
DECLARE
    r ztest_schema1.example_enum;
BEGIN
    select 'ENUM_CONST_1'::example_enum into r;
    return r;
END;
$BODY$ LANGUAGE plpgsql;
