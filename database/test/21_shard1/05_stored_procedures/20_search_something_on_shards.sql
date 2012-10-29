CREATE OR REPLACE FUNCTION search_something_on_shards(p_param text)
          RETURNS integer AS
$BODY$
BEGIN
    IF p_param = 'A' THEN
        RETURN 1;
    END IF;
    RETURN NULL;
END;
$BODY$
          LANGUAGE plpgsql VOLATILE
          COST 100 SECURITY DEFINER;
