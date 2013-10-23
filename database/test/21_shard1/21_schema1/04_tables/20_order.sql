CREATE TABLE ztest_schema1."order" (
    id serial primary key,
    order_number text,
    amount ztest_schema1.monetary_amount,
    address ztest_schema1.address_type default null,
    address_type ztest_schema1.address_type
);

CREATE TABLE ztest_schema1.order_position (
    id serial primary key,
    order_id int references ztest_schema1."order" (id),
    amount ztest_schema1.monetary_amount,
    optional_amount ztest_schema1.monetary_amount,
    address ztest_schema1.address_type
);

INSERT INTO ztest_schema1."order" ( order_number, amount ) SELECT 'order1', (1234.567, 'EUR')::ztest_schema1.monetary_amount;
