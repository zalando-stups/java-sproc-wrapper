CREATE OR REPLACE FUNCTION get_boolean()
          RETURNS boolean AS
        ' begin return true; end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
