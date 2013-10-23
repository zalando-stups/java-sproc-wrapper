CREATE OR REPLACE FUNCTION create_or_update_multiple_objects_with_map(
    p_objs example_domain_object_with_map[]
    )
RETURNS text AS
$BODY$
    SELECT string_agg('<' || a || '_' || array_to_string(akeys(b), '|') || '_' || array_to_string(avals(b), '|') || '>', ',')
        FROM unnest($1) as u(a, b)
$BODY$
LANGUAGE sql IMMUTABLE COST 100;
