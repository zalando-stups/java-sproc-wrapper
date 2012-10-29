CREATE OR REPLACE FUNCTION create_or_update_multiple_objects_with_random_fields(
    p_obj example_domain_object_with_random_fields[]
    )
RETURNS text AS
$BODY$
DECLARE
    result text;
    l_inner example_domain_object_with_random_fields;
begin
    result = '';
    FOR l_idx IN 1 .. COALESCE(array_upper(p_obj, 1), 0) LOOP
        l_inner = p_obj[l_idx];
        result = result || l_inner.x || l_inner.y || l_inner.z;
    END LOOP;
    return result;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
