java-sproc-wrapper
==========================

Library to make PostgreSQL stored procedures(SProcs) available through simple Java "SProcService" interfaces including automatic object serialization and deserialization (using typemapper and convention-over-configuration).

Supports horizontal database sharding (partition/access logic lies within application), easy use of pg_advisory_lock through annotations to ensure single Java node execution, configurable statement timeouts per stored procedure, and PostgreSQL types including enums and hstore.

Dependencies:
-------------

 * Spring Framework
 * PostgreSQL driver ;)
 * Google Guava
 * and more see pom.xml for details

How to run integration tests
----------------------------

* install local PostgreSQL cluster running on 5432
* allow connection with postgres/postgres or create a new user and set config values in pom.xml and application.test.properties
 - create schema and database privileges required for test cases
* make sure you have hstore extension installed, preferably in template1
* create zalando_test database on your localhost PostgreSQL cluster
 - possibly add hstore extension to database
* run integration tests (JUnit tests with database access):
    mvn clean test -Pintegration-test


Known issues:
-------------

* PostgreSQL JDBC driver does not honor identical type names in different schemas, this may lead to issues if typemapper is used where types are present with equal name in more than one schema (this problem is solved now with the commit [3ca94e64d6322fa91c477200bfb3719deaeac153](https://github.com/pgjdbc/pgjdbc/commit/3ca94e64d6322fa91c477200bfb3719deaeac153) to [pgjdbc](https://github.com/pgjdbc/pgjdbc/) driver)
* PostgreSQL domains are not supported as for now
* PostgreSQL `hstore` type is mapped from and to `Map<String,String>`, there is no way to use `Map` of different types for now
* Two different datasources with the same jdbc URL and different search paths might not work properly when we have types with the identical name.
* SprocWrapper relies on the search path to resolve conflicting types with the same name (right now, we are not checking the schema). If one specifies the schema of the stored procedure's return type, SprocWrapper might end up using the wrong one, because it will use the search_path to resolve the conflict. For more info check test: SimpleIT.testTypeLookupBugWithSchema.

License
-------

Copyright 2012-2013 Zalando GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
