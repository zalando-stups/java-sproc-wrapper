package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.Objects;

public class Customer {

    @DatabaseField
    private long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private CustomerAddress address;

    public long getId() {
        return id;
    }

    public Customer setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Customer setName(String name) {
        this.name = name;
        return this;
    }

    public CustomerAddress getAddress() {
        return address;
    }

    public Customer setAddress(CustomerAddress address) {
        this.address = address;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id && Objects.equals(name, customer.name) &&
               Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address);
    }
}
