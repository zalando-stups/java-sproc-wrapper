CREATE OR REPLACE FUNCTION get_entity_with_embed_empty_string(
  OUT x                      TEXT,
  OUT a                      TEXT,
  OUT b                      TEXT
)
RETURNS RECORD AS
$BODY$
begin
    select 'x', null, '' into x, a, b;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;