CREATE OR REPLACE FUNCTION collect_data_using_auto_partition2(p_keys example_sharded_object[], p_additional integer)
    RETURNS SETOF text AS
$BODY$
BEGIN
    RETURN NEXT 'shard2row1' || (p_keys[1]).key || (p_keys[1]).value || p_additional::text;
    RETURN;
END;
$BODY$
    LANGUAGE plpgsql VOLATILE
    COST 100;
