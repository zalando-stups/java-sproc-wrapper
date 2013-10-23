CREATE OR REPLACE FUNCTION test_sproc_call_without_validation_1(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_without_validation_2(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_with_validation(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_without_validation(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_with_validation_1(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_with_validation_2(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_with_validation_3(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_with_validation_invalid_ret_1(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  a.a := null;
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE FUNCTION test_sproc_call_with_validation_invalid_ret_2(a example_domain_object_with_validation)
RETURNS example_domain_object_with_validation AS
$$
BEGIN
  a.a := null;
  return a;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;