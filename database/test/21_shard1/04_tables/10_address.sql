CREATE TABLE address (
 a_id serial PRIMARY KEY,
 a_customer_id integer NOT NULL,
 a_street text,
 a_number text
);
