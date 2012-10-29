CREATE TYPE complex_date AS (
  date_without_time_zone timestamp,
  date_with_time_zone timestamp with time zone,
  date_without_time_zone_transformed timestamp,
  date_with_time_zone_transformed timestamp with time zone
);
