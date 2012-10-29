CREATE TYPE example_domain_object_with_inner_object AS (
  a text,
  b example_domain_object,
  c example_domain_object[]
);
