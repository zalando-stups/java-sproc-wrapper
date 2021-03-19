CREATE OR REPLACE FUNCTION get_example_enum_domain_object(IN param_id INT)
    RETURNS example_enum_domain_object AS
$BODY$

SELECT et_id,
       et_enum_array
FROM enum_table
WHERE et_id = $1;

$BODY$
    LANGUAGE 'sql' VOLATILE
                   SECURITY DEFINER
                   COST 100;