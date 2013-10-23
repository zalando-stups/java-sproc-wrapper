CREATE OR REPLACE FUNCTION get_list_complex_objects()
   RETURNS SETOF example_domain_object AS
$BODY$
DECLARE
    a ztest_schema1.example_domain_object;
    b ztest_schema1.example_domain_object;
    c ztest_schema1.example_domain_object;
BEGIN
      SELECT 'a1' as a, 'b1' as b INTO a;
      SELECT 'a2' as a, 'b2' as b INTO b;
      SELECT 'a3' as a, 'b3' as b INTO c;

      RETURN NEXT a;
      RETURN NEXT b;
      RETURN NEXT c;
END;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;