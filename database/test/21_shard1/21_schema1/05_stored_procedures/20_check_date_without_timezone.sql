CREATE OR REPLACE FUNCTION check_date_without_time_zone(a timestamp)
RETURNS timestamp AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;