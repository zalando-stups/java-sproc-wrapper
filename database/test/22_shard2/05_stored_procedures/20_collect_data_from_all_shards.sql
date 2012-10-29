CREATE OR REPLACE FUNCTION collect_data_from_all_shards(p_some_param text)
    RETURNS SETOF text AS
$BODY$
BEGIN
    PERFORM pg_sleep(0.5);
    RETURN NEXT 'shard2row1';
    RETURN NEXT 'shard2row2';
END;
$BODY$
    LANGUAGE plpgsql VOLATILE
    COST 100;
