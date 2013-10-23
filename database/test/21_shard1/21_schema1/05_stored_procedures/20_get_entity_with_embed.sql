CREATE OR REPLACE FUNCTION get_entity_with_embed(
  OUT x                      TEXT,
  OUT a                      TEXT,
  OUT b                      TEXT
)
RETURNS RECORD AS
$BODY$
begin
    select 'x', 'a', 'b' into x, a, b;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;