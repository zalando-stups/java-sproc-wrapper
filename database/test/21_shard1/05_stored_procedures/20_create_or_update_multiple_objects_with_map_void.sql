CREATE OR REPLACE FUNCTION create_or_update_multiple_objects_with_map_void(
    p_objs example_domain_object_with_map[]
    )
RETURNS void AS
$BODY$
BEGIN
    -- noop
END
$BODY$
LANGUAGE plpgsql IMMUTABLE COST 100;
