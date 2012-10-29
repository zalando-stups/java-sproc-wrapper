zalando-sproc-wrapper-beta
==========================

Library to make PostgreSQL stored procedures available through simple Java "*SProcService" interfaces including automatic object serialization and deserialization (using typemapper and convention-over-configuration). Supports sharding, advisory locking, statement timeouts and PostgreSQL types such as enums and hstore.

How to run integration tests
----------------------------

* install local postgres cluster running on 5432 with postgres/postgres superuser
* create zalando_test database on your localhost postgres cluster
* run integration tests (JUnit tests with database access):

    mvn clean test -Pintegration-test
