CREATE TABLE ztest_schema1.basic_table (
 bt_id serial primary key,
 bt_key text,
 bt_value text );

insert into ztest_schema1.basic_table ( bt_key, bt_value ) select 'key1','value1' union all select 'key2','value2';
