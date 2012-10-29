CREATE OR REPLACE FUNCTION test_global_transformer_6(a timestamp)
RETURNS timestamp AS
$$
BEGIN
  RETURN a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;