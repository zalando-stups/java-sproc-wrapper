CREATE OR REPLACE FUNCTION create_or_update_object_with_enum(
    p_obj example_domain_object_with_enum
    )
RETURNS text AS
$BODY$
    SELECT ($1).x || ($1).my_enum::text
$BODY$
LANGUAGE sql IMMUTABLE COST 100;
