CREATE OR REPLACE FUNCTION test_global_transformer(a example_domain_object_with_global_transformer)
RETURNS example_domain_object_with_global_transformer AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;