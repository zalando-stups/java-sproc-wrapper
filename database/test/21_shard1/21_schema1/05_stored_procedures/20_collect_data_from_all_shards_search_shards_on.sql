CREATE OR REPLACE FUNCTION collect_data_from_all_shards_search_shards_on(p_some_param text)
    RETURNS SETOF text AS
$BODY$
BEGIN
    PERFORM  pg_sleep(0.5);
    RETURN;
END;
$BODY$
    LANGUAGE plpgsql VOLATILE
    COST 100;
