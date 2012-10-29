CREATE OR REPLACE FUNCTION test_global_transformer_5(a text[], b example_domain_object)
RETURNS SETOF text AS
$$
BEGIN
  RETURN QUERY select unnest(a);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;