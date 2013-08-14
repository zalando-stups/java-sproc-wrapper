CREATE OR REPLACE FUNCTION get_entity_with_embed_null_fields(
  OUT x                      TEXT,
  OUT a                      TEXT,
  OUT b                      TEXT
)
RETURNS RECORD AS
$BODY$
begin
    select 'x', null, null into x, a, b;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;