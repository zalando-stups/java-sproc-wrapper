CREATE TYPE t_customer AS
(
  id int,
  name text,
  first_name text,
  default_address t_address,
  addresses t_address[]
);
