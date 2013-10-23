CREATE TYPE order_position_type AS (
    amount          ztest_schema1.monetary_amount,
    optional_amount ztest_schema1.monetary_amount,
    address         ztest_schema1.address_type
);

CREATE TYPE order_type AS (
    order_number    text,
    amount          ztest_schema1.monetary_amount,
    address         ztest_schema1.address_type,
    positions       ztest_schema1.order_position_type[]
);