CREATE FUNCTION get_empty_list(p_objs example_domain_object_with_inner_object[]) RETURNS setof example_domain_object_with_inner_object AS
$$
BEGIN
  IF array_length(p_objs, 1) <> 0 THEN
    RAISE EXCEPTION 'Array length is not 0';
  END IF;

  RETURN;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
