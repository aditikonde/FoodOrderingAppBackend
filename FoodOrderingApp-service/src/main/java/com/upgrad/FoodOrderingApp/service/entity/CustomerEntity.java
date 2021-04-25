package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CUSTOMER")
@NamedQueries({
        @NamedQuery(name = "customerByContactNum", query = "select c from CustomerEntity c where " +
                "c.contactNumber = :contactNumber"),
        @NamedQuery(name = "customerByUuid", query = "select c from CustomerEntity c where c.uuid = :uuid"),
        @NamedQuery(name = "customerById", query = "select c from CustomerEntity c where c.id = :id")

})
public class CustomerEntity {

    public CustomerEntity() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 100)
    private String uuid;

    @Column(name = "firstname")
    @NotNull
    @Size(max = 200)
    private String firstName;

    @Column(name = "lastname")
    @Size(max = 200)
    private String lastName;

    @Column(name = "email")
    @Size(max = 200)
    private String email;

    @Column(name = "contact_number")
    @NotNull
    @Size(max = 100)
    private String contactNumber;

    @Column(name = "password")
    @NotNull
    @Size(max = 200)
    private String password;

    @Column(name = "salt")
    @NotNull
    @Size(max = 200)
    private String salt;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return "CustomerEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
