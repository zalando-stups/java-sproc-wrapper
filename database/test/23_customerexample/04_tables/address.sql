CREATE TABLE customerexample.address
(
 a_id serial primary key,
 a_customer_id int,
 a_street text,
 a_city text,
 a_zip text,
 a_street_number int
);