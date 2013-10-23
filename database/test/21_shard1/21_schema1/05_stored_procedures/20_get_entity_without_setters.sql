CREATE OR REPLACE FUNCTION get_entity_without_setters(
  OUT a                      TEXT,
  OUT b                      TEXT,
  OUT c                      TEXT
)
RETURNS RECORD AS
$BODY$
begin
    select md5(random()::text),
           md5(random()::text),
           md5(random()::text)
      into a,
           b,
           c;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;