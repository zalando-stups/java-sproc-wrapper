
CREATE OR REPLACE FUNCTION get_simple_int_void(p_param int)
          RETURNS VOID AS
        ' begin end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
