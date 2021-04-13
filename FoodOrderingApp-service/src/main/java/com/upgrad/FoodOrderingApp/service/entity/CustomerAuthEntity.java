package com.upgrad.FoodOrderingApp.service.entity;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "CUSTOMER_AUTH")
public class CustomerAuthEntity {

    public CustomerAuthEntity() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "uuid")
    private String uuid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Column(name = "access_token")
    @Size(max = 500)
    private String access_token;

    @Column(name = "login_at")
    private ZonedDateTime login_at;

    @Column(name = "logout_at")
    private ZonedDateTime logout_at;

    @Column(name = "expires_at")
    private ZonedDateTime expires_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public ZonedDateTime getLogin_at() {
        return login_at;
    }

    public void setLogin_at(ZonedDateTime login_at) {
        this.login_at = login_at;
    }

    public ZonedDateTime getLogout_at() {
        return logout_at;
    }

    public void setLogout_at(ZonedDateTime logout_at) {
        this.logout_at = logout_at;
    }

    public ZonedDateTime getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(ZonedDateTime expires_at) {
        this.expires_at = expires_at;
    }

    @Override
    public String toString() {
        return "CustomerAuthEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", customer=" + customer +
                ", access_token='" + access_token + '\'' +
                ", login_at=" + login_at +
                ", logout_at=" + logout_at +
                ", expires_at=" + expires_at +
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
