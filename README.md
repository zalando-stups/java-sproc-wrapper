SProcWrapper
============

[![Build Status](https://travis-ci.org/zalando/java-sproc-wrapper.svg)](https://travis-ci.org/zalando/java-sproc-wrapper)

Library to make PostgreSQL stored procedures(SProcs) available through simple Java "SProcService" interfaces including automatic object serialization and deserialization (using typemapper and convention-over-configuration).

Supports horizontal database sharding (partition/access logic lies within application), easy use of pg_advisory_lock through annotations to ensure single Java node execution, configurable statement timeouts per stored procedure, and PostgreSQL types including enums and hstore.

Type Mapping
------------

SProcWrapper provides an efficient and easy-to-use mechanism for translating values from database to Java objects and vice-versa. It allows us to map not only primitive types, but also complex types (Java domain objects).

Here are some examples!

Using basic types:

```java
@SProcService
public interface CustomerSProcService {
  @SProcCall
  int registerCustomer(@SProcParam String name, @SProcParam String email);
}
```

```sql
CREATE FUNCTION register_customer(p_name text, p_email text)
RETURNS int AS
$$
  INSERT INTO z_data.customer(c_name, c_email)
       VALUES (p_name, p_email)
    RETURNING c_id
$$
LANGUAGE 'sql' SECURITY DEFINER;
```

And a complex type:

```java
@SProcService
public interface OrderSProcService {
  @SProcCall
  List<Order> findOrders(@SProcParam String email);
}
```

```sql
CREATE FUNCTION find_orders(p_email text,
  OUT order_id int,
  OUT order_date timestamp,
  OUT shipping_address order_address)
RETURNS SETOF RECORD AS
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

Please check [unit/integration tests](src/test/java/de/zalando/sprocwrapper) for more examples.

The following table shows the mapping between a database type and a Java type:

| Database         | Java Type                                         |
| ---------------- | ------------------------------------------------- |
| smallint         | int                                               |
| integer          | int                                               |
| bigint           | long                                              |
| decimal          | java.math.BigDecimal                              |
| numeric          | java.math.BigDecimal                              |
| real             | float                                             |
| double precision | double                                            |
| serial           | int                                               |
| bigserial        | long                                              |
| varchar          | java.lang.String                                  |
| char             | char                                              |
| text             | java.lang.String                                  |
| timestamp        | java.sql.Timestamp                                |
| timestamptz      | java.sql.Timestamp                                |
| date             | java.sql.Timestamp                                |
| time             | java.sql.Timestamp                                |
| timetz           | java.sql.Timestamp                                |
| boolean          | boolean                                           |
| enum             | java.lang.Enum                                    |
| array            | java.util.List / java.util.Set                    |
| hstore           | java.util.Map<java.lang.String, java.lang.String> |

Note: SProcwrapper doesn't support functions returning arrays as a single output. If one wants to return a collection, please return a SETOF instead.

Prerequisites
-------------

 * Java 7
 * To compile, one should use [Maven](http://maven.apache.org/) 2.1.0 or above

Dependencies
------------

 * Spring Framework
 * PostgreSQL JDBC driver ;)
 * Google Guava
 * and more see `pom.xml` for details

How to run integration tests
----------------------------

The provided helper script will start a PostgreSQL instance with Docker on port 5432 and run integration tests:

    ./test.sh

You can use the provided Vagrant box to run the script in.

Known issues
------------

* PostgreSQL JDBC driver does not honor identical type names in different schemas, this may lead to issues if typemapper is used where types are present with equal name in more than one schema (this problem is solved now with the commit [3ca94e64d6322fa91c477200bfb3719deaeac153](https://github.com/pgjdbc/pgjdbc/commit/3ca94e64d6322fa91c477200bfb3719deaeac153) to [pgjdbc](https://github.com/pgjdbc/pgjdbc/) driver);
* PostgreSQL domains are not supported as for now;
* PostgreSQL `hstore` type is mapped from and to `Map<String,String>`, there is no way to use `Map` of different types for now;
* Two different datasources with the same JDBC URL and different search paths might not work properly when we have types with the identical name;
* SProcWrapper relies on the search path to resolve conflicting types with the same name (right now, we are not checking the schema). If one specifies the schema of the stored procedure's return type, SProcWrapper might end up using the wrong one, because it will use the `search_path` to resolve the conflict. For more info check test: `SimpleIT.testTypeLookupBugWithSchema`;
* For integration with Spring's transaction management use the `TransactionAwareDataSourceProxy` as the data source injected into the data source provider.

Documentation
-------------

You can find some more information about the SProcWrapper in our various Zalando Technology blog posts:

* http://tech.zalando.com/goto-2013-why-zalando-trusts-in-postgresql/
* http://tech.zalando.com/zalando-stored-procedure-wrapper-part-i/
* http://tech.zalando.com/files/2013/04/jug_dortmund_april_2013.pdf


License
-------

Copyright 2012-2014 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
