CREATE OR REPLACE FUNCTION get_simple_long()
          RETURNS integer AS
        ' begin return 4; end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
