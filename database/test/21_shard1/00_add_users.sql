CREATE FUNCTION _create_role(name text) RETURNS VOID AS
$$
BEGIN

  IF NOT EXISTS( SELECT 1 FROM pg_roles WHERE rolname = name )
  THEN
    EXECUTE 'CREATE ROLE ' || name || ';';
  END IF;

END;
$$
LANGUAGE 'plpgsql';

DO $$
BEGIN
  PERFORM _create_role('data_owner');
  PERFORM _create_role('api_owner');
  PERFORM _create_role('data_reader');
  PERFORM _create_role('data_writer');
  PERFORM _create_role('api_executor');
  PERFORM _create_role('zalando_api');
  PERFORM _create_role('api_usage');
  PERFORM _create_role('data_usage');

  GRANT data_usage TO data_reader;
  GRANT api_usage TO api_executor;

  GRANT data_writer TO api_owner;
  GRANT data_reader TO api_owner;

  GRANT api_executor TO zalando_api;

  ALTER ROLE zalando_api WITH LOGIN PASSWORD 'zalando_api';
END;
$$;

DROP FUNCTION _create_role(text);
