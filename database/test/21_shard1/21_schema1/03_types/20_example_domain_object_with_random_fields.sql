CREATE TYPE example_domain_object_with_random_fields AS (
  z int,
  y text,
  x text,
  inner_object example_domain_object_with_random_fields_inner,
  list example_domain_object_with_random_fields_inner[]
);
