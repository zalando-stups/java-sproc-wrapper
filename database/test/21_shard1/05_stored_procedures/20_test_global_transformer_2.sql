CREATE OR REPLACE FUNCTION test_global_transformer_2(a text)
RETURNS text AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;