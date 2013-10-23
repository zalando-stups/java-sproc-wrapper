
CREATE OR REPLACE FUNCTION get_simple_int(p_param int)
          RETURNS integer AS
        ' begin return p_param; end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
