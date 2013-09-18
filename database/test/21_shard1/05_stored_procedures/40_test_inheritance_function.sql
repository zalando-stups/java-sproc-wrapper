CREATE OR REPLACE FUNCTION test_inheritance_function ( p_in test_inheritance ) RETURNS int AS
$$
DECLARE
BEGIN
  RETURN p_in.a + p_in.b + p_in.c;
END;
$$ LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER;
