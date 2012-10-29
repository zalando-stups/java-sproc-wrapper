CREATE FUNCTION get_address(a address_type) RETURNS address_type AS
$$
DECLARE
  b address_type;
BEGIN
  select a_id,a_customer_id,a_street,a_number into b from address where a_id = a.id;
  return b;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;


CREATE FUNCTION get_address_sql(a address_type) RETURNS address_type AS
$$
  select (a_id,a_customer_id,a_street,a_number)::address_type from address where a_id = ($1).id;
$$ LANGUAGE sql SECURITY DEFINER;
