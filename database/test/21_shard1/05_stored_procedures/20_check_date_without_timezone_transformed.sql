CREATE OR REPLACE FUNCTION check_date_without_time_zone_transformed(a timestamp)
RETURNS timestamp with time zone AS
$$
BEGIN
  return a::timestamp with time zone;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;