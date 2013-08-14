package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType(name = "address_type")
public class AddressPojo {

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final int customerId) {
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @DatabaseField
    public Integer id;

    @DatabaseField
    public int customerId = 0;

    @DatabaseField
    public String street = "";

    @DatabaseField
    public String number = "";

    public String toString() {
        return "AddressPojo [" + id + "," + customerId + "," + street + "," + number + "]";
    }
}
