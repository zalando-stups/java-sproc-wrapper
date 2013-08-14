/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.sprocwrapper.customerexample;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  Jan
 */
@DatabaseType(name = "t_address")
public class Address {
    @DatabaseField
    protected Integer id;
    @DatabaseField
    protected String street;
    @DatabaseField
    protected String city;
    @DatabaseField
    protected String zip;
    @DatabaseField
    protected String streetNumber;
    @DatabaseField
    protected int customerId;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final int customerId) {
        this.customerId = customerId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(final String streetNumber) {
        this.streetNumber = streetNumber;
    }
}
