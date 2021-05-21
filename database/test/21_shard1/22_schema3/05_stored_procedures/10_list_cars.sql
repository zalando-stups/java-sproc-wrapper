CREATE OR REPLACE FUNCTION get_all_cars()
    RETURNS SETOF ztest_schema3.CAR AS
$body$
BEGIN
    RETURN QUERY SELECT t.id, t.brand,
                        t.color
                 FROM (VALUES (1::BIGINT, 'Toyota'::TEXT, 'RED'::ztest_schema3.COLOR),
                              (2::BIGINT, 'Mercedes'::TEXT, 'BLUE'::ztest_schema3.COLOR))
                          AS t (id, brand, color);
END;
$body$
    LANGUAGE plpgsql
    SECURITY DEFINER
    COST 100;
