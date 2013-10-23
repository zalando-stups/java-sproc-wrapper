CREATE OR REPLACE FUNCTION get_example_2_entity_with_numbers_1()
   RETURNS example2_domain_object1 AS
$BODY$
DECLARE
    r example2_domain_object1;
BEGIN
      SELECT 'example2field1',
           (SELECT row('example2complexfield1', 'example2complexfield2')::example2_domain_object2)
      INTO r;

    RETURN r;
END;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;