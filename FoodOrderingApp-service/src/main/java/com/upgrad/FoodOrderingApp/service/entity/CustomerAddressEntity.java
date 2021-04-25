package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "CUSTOMER_ADDRESS")
@NamedQueries({

        @NamedQuery(name = "custAddressByCustIdAddressId", query = "select ca from " +
                "CustomerAddressEntity ca where ca.customer=:customer and ca.address=:address"),

        @NamedQuery(name = "custAddressByAddressId", query = "select q from CustomerAddressEntity q where q" +
                " .id = :addressId"),
        @NamedQuery(name = "getAddressByCustomer", query = "select q from CustomerAddressEntity q where q" +
                " .customer = :customer"),
        @NamedQuery(name = "getAddressesByCustomerId", query = "select ca from CustomerAddressEntity ca " +
                "inner join ca.address add inner join ca.customer cust where cust.id = :id")
        //select ci.address from CustomerAddressEntity ci" +
        //                "inner join ci.address add inner join ci.customer cust  where cust.id = :id

})
public class CustomerAddressEntity implements Serializable {

    public CustomerAddressEntity() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CustomerAddressEntity{" +
                "id=" + id +
                ", customer=" + customer +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

}
