CREATE OR REPLACE FUNCTION use_integer_list_param(p_param integer[])
          RETURNS VOID AS
        ' begin  end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
