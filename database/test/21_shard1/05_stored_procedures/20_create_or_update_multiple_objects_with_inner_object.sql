CREATE OR REPLACE FUNCTION create_or_update_multiple_objects_with_inner_object(
    p_objs example_domain_object_with_inner_object[]
    )
RETURNS text AS
$BODY$
    SELECT string_agg('<' || a || '_' || (b).a ||  '|' || (b).b || '>', ',')
        FROM unnest($1) as u(a, b)
$BODY$
LANGUAGE sql IMMUTABLE COST 100;
