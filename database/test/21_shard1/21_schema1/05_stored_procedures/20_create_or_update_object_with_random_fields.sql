CREATE OR REPLACE FUNCTION create_or_update_object_with_random_fields(
    p_obj example_domain_object_with_random_fields
    )
RETURNS text AS
$BODY$
DECLARE
    result text;
    l_inner example_domain_object_with_random_fields_inner;
begin
    result = p_obj.x || p_obj.y || p_obj.z || (p_obj.inner_object).x || (p_obj.inner_object).y || (p_obj.inner_object).z;
    result = result || '(';
    FOR l_idx IN 1 .. COALESCE(array_upper(p_obj.list, 1), 0) LOOP
        l_inner = (p_obj.list)[l_idx];
        result = result || '<' || l_inner.x || l_inner.y || l_inner.z || '>';
    END LOOP;
    result = result || ')';
    return result;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
