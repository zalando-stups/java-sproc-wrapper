CREATE OR REPLACE FUNCTION create_or_update_multiple_objects(
    p_objs example_domain_object[]
    )
RETURNS text AS
$BODY$
DECLARE
    l_idx integer;
    l_str text;
begin
    l_str := '';
    FOR l_idx IN 1 .. COALESCE(array_upper(p_objs, 1), 0) LOOP
        l_str := l_str || p_objs[l_idx].a || '_' || p_objs[l_idx].b || ',';
    END LOOP;
    return l_str;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
