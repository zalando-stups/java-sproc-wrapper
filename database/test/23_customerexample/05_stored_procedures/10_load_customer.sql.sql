CREATE OR REPLACE FUNCTION load_customer(p_customer_id int) RETURNS SETOF t_customer
AS
$$
SELECT c_id,c_name,c_first_name,
       CASE WHEN c_default_address_id IS NOT NULL
            THEN
                (SELECT (a_id,a_customer_id,a_street,a_city,a_zip,a_street_number)::t_address
                   FROM customerexample.address
                  WHERE a_id = c_default_address_id)
            ELSE null::t_address END,
       ARRAY(SELECT (a_id,a_customer_id,a_street,a_city,a_zip,a_street_number)::t_address
               FROM customerexample.address
              WHERE a_customer_id = c_id)::t_address[]
  FROM customerexample.customer WHERE c_id = $1
$$
LANGUAGE 'sql' SECURITY DEFINER;
