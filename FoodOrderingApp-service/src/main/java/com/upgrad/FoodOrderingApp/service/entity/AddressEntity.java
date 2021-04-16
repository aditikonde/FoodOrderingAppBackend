package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ADDRESS")
@NamedQueries({
        @NamedQuery(name = "getAddressByUuid", query = "select a from AddressEntity a where a" +
                ".uuid " + "= :uuid"),
        @NamedQuery(name = "deleteAddress", query = "delete from AddressEntity q where q.uuid = " +
                ":uuid")
})
public class AddressEntity {
    public AddressEntity() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 64)
    private String uuid;

    @Column(name = "flat_buil_number")
    @Size(max = 100)
    private String flat_buil_number;

    @Column(name = "locality")
    @Size(max = 100)
    private String locality;

    @Column(name = "city")
    @Size(max = 64)
    private String city;

    @Column(name = "pincode")
    @Size(max = 64)
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateEntity state;

    @Column(name = "active")
    private Integer active;

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

    public String getFlat_buil_number() {
        return flat_buil_number;
    }

    public void setFlat_buil_number(String flat_buil_number) {
        this.flat_buil_number = flat_buil_number;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", flat_buil_number='" + flat_buil_number + '\'' +
                ", locality='" + locality + '\'' +
                ", city='" + city + '\'' +
                ", pincode='" + pincode + '\'' +
                ", state=" + state +
                ", active=" + active +
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
