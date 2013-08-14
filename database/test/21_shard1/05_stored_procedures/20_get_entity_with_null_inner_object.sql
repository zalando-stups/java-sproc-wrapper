CREATE OR REPLACE FUNCTION get_entity_with_null_inner_object()
   RETURNS example_domain_object_with_inner_object AS
$BODY$
DECLARE
     r example_domain_object_with_inner_object;
BEGIN
    SELECT 'a',
           (SELECT row('a', 'b')::example_domain_object WHERE false),
           null
      INTO r;

    RETURN r;
END;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;