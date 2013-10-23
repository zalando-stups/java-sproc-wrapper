CREATE OR REPLACE FUNCTION check_date_with_time_zone(a timestamp with time zone)
RETURNS timestamp with time zone AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;