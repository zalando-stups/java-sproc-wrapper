CREATE OR REPLACE FUNCTION get_example_1_entity_with_numbers_1(r example_1_domain_object_1)
   RETURNS example_1_domain_object_1 AS
$BODY$
BEGIN
    RETURN r;
END;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;