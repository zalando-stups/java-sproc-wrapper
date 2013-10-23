CREATE FUNCTION create_or_update_address(a address_type) RETURNS address_type
AS
$$
DECLARE
BEGIN
  IF a.id IS NULL THEN
    INSERT INTO address ( a_customer_id , a_street, a_number ) VALUES ( a.customer_id , a.street, a."number" ) RETURNING a_id INTO a.id;
    RETURN a;
  ELSE
    RAISE EXCEPTION 'Trying to create address, but id is already present!';
  END IF;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
