CREATE OR REPLACE FUNCTION get_partial_object(p partial_object)
RETURNS partial_object AS
$$
DECLARE
    r partial_object;
BEGIN
  r.name := p.name;
  return r;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;