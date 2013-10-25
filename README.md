SProcWrapper
============

Library to make PostgreSQL stored procedures(SProcs) available through simple Java "SProcService" interfaces including automatic object serialization and deserialization (using typemapper and convention-over-configuration).

Supports horizontal database sharding (partition/access logic lies within application), easy use of pg_advisory_lock through annotations to ensure single Java node execution, configurable statement timeouts per stored procedure, and PostgreSQL types including enums and hstore.

Type Mapping
------------

The SprocWrapper provides and efficient and easy-to-use mechanimism for translatiing values from database to Java objects and vice-versa. It allows us to map not only primitive types, but also complex types (Java domain objects).

Here are some examples.

Basic type:

```java
@SProcCall
String getNameForId(@SProcParam int id);
```

```sql
CREATE FUNCTION get_name_for_id(p_id int) RETURNS text AS
$$
SELECT name
  FROM customer
 WHERE id = p_id
$$ LANGUAGE ‘sql’;
```

Complex type:

```java
@SProcCall
List<Order> findOrders(@SProcParam String email);
```

```sql
CREATE FUNCTION find_orders(p_email text,
  OUT order_id int,
  OUT order_date timestamp,
  OUT shipping_address order_address) RETURNS SETOF RECORD AS
$$
SELECT o_id,
       o_date,
       ROW(oa_street, oa_city, oa_country)::order_address
  FROM z_data.order
  JOIN z_data.order_address
    ON oa_order_id = o_id
  JOIN z_data.customer
    ON c_id = o_customer_id
 WHERE c_email = p_email
$$
LANGUAGE 'sql' SECURITY DEFINER;
```

Supported data types:

Numeric Types:

| Database         | JAVA                 |
| ---------------- | -------------------- |
| smallint         | int                  |
| integer          | int                  |
| bigint           | long                 |
| decimal          | java.math.BigDecimal |
| numeric          | java.math.BigDecimal |
| real             | float                |
| double precision | double               |
| serial           | int                  |
| bigserial        | long                 |

Character Types:

| Database         | JAVA                 |
| ---------------- | -------------------- |
| varchar          | String               |
| char             | char                 |
| text             | String               |

Date/Time Types:

| Database         | JAVA                 |
| ---------------- | -------------------- |
| timestamp        | java.sql.Timestamp   |
| date             | java.sql.Timestamp   |
| time             | java.sql.Timestamp   |

Boolean Type:

| Database         | JAVA                 |
| ---------------- | -------------------- |
| boolean          | boolean              |

Enumerated Types:

| Database         | JAVA                 |
| ---------------- | -------------------- |
| enum             | java.lang.Enum       |

Container types:

| Database         | JAVA                          |
| ---------------- | ----------------------------- |
| array            | java.util.List/java.util.Set  |
| hstore           | java.util.Map<String, String> |


Note: Sprocwrapper doesn't support functions returning arrays as a single output. If one wants to return a collection, please return a SETOF instead.

Please check unit/integration tests for more examples.

Prerequisites
-------------

 * Java 7
 * To compile, one should use [Maven](http://maven.apache.org/) 2.1.0 or above

Dependencies
------------

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


Known issues
------------

* PostgreSQL JDBC driver does not honor identical type names in different schemas, this may lead to issues if typemapper is used where types are present with equal name in more than one schema (this problem is solved now with the commit [3ca94e64d6322fa91c477200bfb3719deaeac153](https://github.com/pgjdbc/pgjdbc/commit/3ca94e64d6322fa91c477200bfb3719deaeac153) to [pgjdbc](https://github.com/pgjdbc/pgjdbc/) driver)
* PostgreSQL domains are not supported as for now
* PostgreSQL `hstore` type is mapped from and to `Map<String,String>`, there is no way to use `Map` of different types for now
* Two different datasources with the same jdbc URL and different search paths might not work properly when we have types with the identical name.
* SprocWrapper relies on the search path to resolve conflicting types with the same name (right now, we are not checking the schema). If one specifies the schema of the stored procedure's return type, SprocWrapper might end up using the wrong one, because it will use the search_path to resolve the conflict. For more info check test: SimpleIT.testTypeLookupBugWithSchema.
* For integration with Spring's transaction management use the TransactionAwareDataSourceProxy as the data source injected into the data source provider.

Documentation
-------------

You can find some more information about the SProcWrapper in our various Zalando Technology blog posts:

* http://tech.zalando.com/goto-2013-why-zalando-trusts-in-postgresql/
* http://tech.zalando.com/zalando-stored-procedure-wrapper-part-i/
* http://tech.zalando.com/files/2013/04/jug_dortmund_april_2013.pdf


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
