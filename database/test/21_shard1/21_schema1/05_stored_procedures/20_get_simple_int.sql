CREATE OR REPLACE FUNCTION get_simple_int()
          RETURNS integer AS
        ' begin return 3; end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
