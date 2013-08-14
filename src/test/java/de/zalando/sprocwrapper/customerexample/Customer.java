package de.zalando.sprocwrapper.customerexample;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  Jan
 */
@DatabaseType(name = "t_customer")
public class Customer {

    @DatabaseField
    protected Integer id;

    @DatabaseField
    protected String name;
    @DatabaseField
    protected String firstName;
    @DatabaseField
    protected List<Address> addresses;

    @DatabaseField
    protected Address defaultAddress;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    public Address getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(final Address address) {
        this.defaultAddress = address;
    }
}
