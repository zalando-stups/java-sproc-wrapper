CREATE OR REPLACE FUNCTION check_date_complex_date(a complex_date)
RETURNS complex_date AS
$$
DECLARE
    r complex_date;
BEGIN
  -- transform the give date without time zone:
  r.date_without_time_zone             := a.date_without_time_zone;
  r.date_with_time_zone                := a.date_with_time_zone;
  r.date_without_time_zone_transformed := a.date_without_time_zone_transformed;
  r.date_with_time_zone_transformed    := a.date_without_time_zone_transformed::timestamp with time zone;
  return r;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;