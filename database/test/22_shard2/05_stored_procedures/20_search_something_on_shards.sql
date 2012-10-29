CREATE OR REPLACE FUNCTION search_something_on_shards(p_param text)
          RETURNS integer AS
$BODY$
BEGIN
    IF p_param = 'B' THEN
        RETURN 2;
    END IF;
    RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100 SECURITY DEFINER;