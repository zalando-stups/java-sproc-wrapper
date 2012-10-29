CREATE TABLE customerexample.customer
(
  c_id serial primary key,
  c_name text,
  c_first_name text,
  c_default_address_id int
);

CREATE INDEX ON customerexample.customer ( c_default_address_id );
