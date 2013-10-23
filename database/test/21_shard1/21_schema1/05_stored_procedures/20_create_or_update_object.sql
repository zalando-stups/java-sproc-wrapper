CREATE OR REPLACE FUNCTION create_or_update_object(
    p_obj example_domain_object
    )
RETURNS text AS
$BODY$
begin
    return p_obj.a || ' ' || p_obj.b;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
