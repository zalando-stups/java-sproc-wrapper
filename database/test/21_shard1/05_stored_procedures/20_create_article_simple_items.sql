CREATE OR REPLACE FUNCTION create_article_simple_items(
    sku text, stockid integer, quantity integer, price integer,
    referencenumber text)
RETURNS text AS
$BODY$
begin
    return sku || ' ' || stockid || ' ' || quantity || ' ' || price || ' ' || referencenumber;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
