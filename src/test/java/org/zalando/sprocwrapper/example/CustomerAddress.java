package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.Objects;

public class CustomerAddress {

    @DatabaseField
    private String street;

    @DatabaseField
    private int houseNumber;

    public String getStreet() {
        return street;
    }

    public CustomerAddress setStreet(String street) {
        this.street = street;
        return this;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public CustomerAddress setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerAddress that = (CustomerAddress) o;
        return houseNumber == that.houseNumber && Objects.equals(street, that.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, houseNumber);
    }
}
