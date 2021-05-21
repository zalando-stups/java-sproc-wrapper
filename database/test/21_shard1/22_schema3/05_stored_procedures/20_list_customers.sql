CREATE OR REPLACE FUNCTION get_all_customers()
    RETURNS SETOF ztest_schema3.CUSTOMER AS
$body$
BEGIN
    RETURN QUERY SELECT t.id, t.name,
                        t.address
                 FROM (VALUES (1::BIGINT, 'John'::TEXT, ROW('First street', 15)::ztest_schema3.CUSTOMER_ADDRESS))
                          AS t (id, name, address);
END;
$body$
    LANGUAGE plpgsql
    SECURITY DEFINER
    COST 100;
