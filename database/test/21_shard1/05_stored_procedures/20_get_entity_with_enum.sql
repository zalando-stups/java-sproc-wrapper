CREATE OR REPLACE FUNCTION get_entity_with_enum(
    p_id bigint
)
RETURNS example_domain_object_with_enum AS
$BODY$
declare
    r ztest_shard1.example_domain_object_with_enum;
    b ztest_shard1.example_domain_object;
begin
    select 'sample y.a', 'sample y.b' into b;
    select 'sample x', b, 'ENUM_CONST_1' into r;

    return r;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;