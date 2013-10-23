CREATE OR REPLACE FUNCTION create_article_simples(
    skus text[]
)
RETURNS VOID AS
$BODY$
begin
    -- NO-OP
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
