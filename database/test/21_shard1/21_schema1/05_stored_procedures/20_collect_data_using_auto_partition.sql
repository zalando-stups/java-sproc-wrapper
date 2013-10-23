CREATE OR REPLACE FUNCTION collect_data_using_auto_partition(p_keys text[])
    RETURNS SETOF text AS
$BODY$
BEGIN
    RETURN NEXT 'shard1row1' || p_keys[1];
    RETURN NEXT 'shard1row2' || p_keys[1];
    RETURN;
END;
$BODY$
    LANGUAGE plpgsql VOLATILE
    COST 100;
