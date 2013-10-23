CREATE OR REPLACE FUNCTION login(p_user text, p_password text)
          RETURNS boolean AS
        ' begin return true; end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
