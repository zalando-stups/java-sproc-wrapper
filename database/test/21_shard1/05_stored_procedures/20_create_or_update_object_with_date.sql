CREATE OR REPLACE FUNCTION create_or_update_object_with_date(
    p_obj example_domain_object_with_date
    )
RETURNS text AS
$BODY$
    SELECT ($1).x || ($1).my_date::text || coalesce(to_char(($1).my_timestamp, 'YYYY-MM-DD HH24:MI:SS'), '')
$BODY$
LANGUAGE sql IMMUTABLE COST 100;
