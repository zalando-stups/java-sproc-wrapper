CREATE OR REPLACE FUNCTION insert_address(
    p_new_data text,
    p_fail_on_shard text
)
RETURNS text AS
$BODY$
declare
begin

    insert into address (a_customer_id, a_street) values (0, p_new_data);

    if p_fail_on_shard = current_database() then
        RAISE EXCEPTION 'insert_new_data_with_transaction should fail';
    end if;

    return p_new_data;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100
SECURITY DEFINER;
