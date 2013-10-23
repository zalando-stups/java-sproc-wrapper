CREATE OR REPLACE FUNCTION test_global_transformer_3(a text, b example_domain_object)
RETURNS text AS
$$
BEGIN
  return a || ':b:' || b.a || b.b;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;