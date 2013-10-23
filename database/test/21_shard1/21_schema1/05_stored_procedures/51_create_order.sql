CREATE OR REPLACE FUNCTION ztest_schema1.create_order(order_number text, amount ztest_schema1.monetary_amount, address ztest_schema1.address_type) RETURNS INT AS
$$
    INSERT INTO ztest_schema1."order" ( order_number, amount, address ) VALUES ( $1 , $2, $3 ) RETURNING id;
$$ LANGUAGE 'sql' SECURITY DEFINER;

CREATE OR REPLACE FUNCTION ztest_schema1.create_order(order_number text, amount ztest_schema1.monetary_amount) RETURNS INT AS
$$
    SELECT ztest_schema1.create_order( $1, $2, NULL::ztest_schema1.address_type);
$$ LANGUAGE 'sql' SECURITY DEFINER;

CREATE OR REPLACE FUNCTION ztest_schema1.create_order(p_order ztest_schema1.order_type) RETURNS INT AS
$$
DECLARE
    l_order_id int;
BEGIN
    SELECT ztest_schema1.create_order($1.order_number, $1.amount, $1.address) INTO l_order_id;

    INSERT INTO ztest_schema1.order_position ( order_id, amount, optional_amount, address ) (SELECT l_order_id, pos.amount, pos.optional_amount, pos.address FROM unnest($1.positions) AS pos);

    RETURN l_order_id;
END;
$$ LANGUAGE 'plpgsql' SECURITY DEFINER;