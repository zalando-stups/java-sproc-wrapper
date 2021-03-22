CREATE TABLE enum_table (
    et_id           serial primary key,
    et_enum_array   ztest_schema1.example_enum[]
);

INSERT INTO enum_table ( et_id, et_enum_array )
VALUES (1, '{}'),
       (2, NULL),
       (3, '{ENUM_CONST_2}'),
       (4, '{ENUM_CONST_1, ENUM_CONST_2}');
