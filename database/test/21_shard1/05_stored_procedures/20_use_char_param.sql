CREATE OR REPLACE FUNCTION use_char_param(p_param char)
          RETURNS VOID AS
        ' begin  end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
