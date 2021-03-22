CREATE OR REPLACE FUNCTION list_example_enum_domain_objects()
    RETURNS SETOF example_enum_domain_object AS
$BODY$

SELECT et_id,
       et_enum_array
FROM enum_table;

$BODY$
    LANGUAGE 'sql' VOLATILE
                   SECURITY DEFINER
                   COST 100;