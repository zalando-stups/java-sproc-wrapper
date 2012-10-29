create or replace function get_null_single_result() returns setof example_domain_object
as
$$
begin
  return;
end;
$$ language plpgsql;
