CREATE FUNCTION find_addresses_by_street(street text) RETURNS setof address_type AS
$$
BEGIN
  return query
  select a_id,a_customer_id,a_street,a_number from address where a_street = street;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
