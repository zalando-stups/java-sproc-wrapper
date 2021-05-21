RESET role;

CREATE SCHEMA ztest_schema3
 AUTHORIZATION zalando_api_owner;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA ztest_schema3 REVOKE EXECUTE ON FUNCTIONS FROM public;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando_api_owner IN SCHEMA ztest_schema3 REVOKE EXECUTE ON FUNCTIONS FROM public;
ALTER DEFAULT PRIVILEGES IN SCHEMA ztest_schema3 REVOKE EXECUTE ON FUNCTIONS FROM public;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando_api_owner IN SCHEMA ztest_schema3 GRANT EXECUTE ON FUNCTIONS TO zalando_api_executor;

GRANT USAGE ON SCHEMA ztest_schema3 TO zalando_api_usage;

SET ROLE TO zalando_api_owner;

SET search_path TO ztest_schema3, public;
