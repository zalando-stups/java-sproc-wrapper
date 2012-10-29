CREATE OR REPLACE FUNCTION create_customer ( p_customer t_customer ) RETURNS SETOF t_customer
AS
$$
DECLARE
  l_address_id int;
  l_customer_id int;
BEGIN
  IF NOT p_customer.default_address IS NULL THEN
    INSERT INTO customerexample.address ( a_street )
      SELECT (p_customer.default_address).street
      RETURNING a_id INTO l_address_id;
  END IF;

  INSERT INTO customerexample.customer ( c_name, c_first_name , c_default_address_id )
    SELECT p_customer.name, p_customer.first_name, l_address_id
    RETURNING c_id INTO l_customer_id;

  IF l_address_id IS NOT NULL THEN
    UPDATE customerexample.address
       SET a_customer_id = l_customer_id
     WHERE a_id = l_address_id;
  END IF;

  RETURN QUERY SELECT * FROM load_customer ( l_customer_id ) LIMIT 1;
END;
$$
LANGUAGE 'plpgsql' SECURITY DEFINER;
