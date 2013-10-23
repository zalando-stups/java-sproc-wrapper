CREATE OR REPLACE FUNCTION use_enum_param(p_param example_enum)
          RETURNS VOID AS
        ' begin  end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
