CREATE OR REPLACE FUNCTION test_simple_transformer(a example_domain_object_with_simple_transformer)
RETURNS example_domain_object_with_simple_transformer AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;