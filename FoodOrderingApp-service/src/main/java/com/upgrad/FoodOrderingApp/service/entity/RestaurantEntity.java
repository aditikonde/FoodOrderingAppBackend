package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "RESTAURANT")
@NamedQueries({
        @NamedQuery(name = "allRestaurants", query = "select c from RestaurantEntity c"),
        @NamedQuery(name = "findRestaurantByUUId",query = "select r from RestaurantEntity r where" +
                " lower(r.uuid) = :restaurantUUID"),
        @NamedQuery(name = "getRestaurantAddress",query = "select r from RestaurantEntity r where lower(r.uuid) = :restaurantUUID")

})
public class RestaurantEntity {

    public RestaurantEntity() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 64)
    private String uuid;

    @Column(name = "restaurant_name")
    @NotNull
    @Size(max = 64)
    private String restaurant_name;

    @Column(name = "photo_url")
    @Size(max = 64)
    private String photo_url;

    @Column(name = "customer_rating")
    @NotNull
    private BigDecimal customer_rating;

    @Column(name = "average_price_for_two")
    @NotNull
    private Integer average_price_for_two;

    @Column(name = "number_of_customers_rated")
    @NotNull
    private Integer number_of_customers_rated;

    @Column(name = "address_id")
    @NotNull
    private AddressEntity address;

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

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public BigDecimal getCustomer_rating() {
        return customer_rating;
    }

    public void setCustomer_rating(BigDecimal customer_rating) {
        this.customer_rating = customer_rating;
    }

    public Integer getAverage_price_for_two() {
        return average_price_for_two;
    }

    public void setAverage_price_for_two(Integer average_price_for_two) {
        this.average_price_for_two = average_price_for_two;
    }

    public Integer getNumber_of_customers_rated() {
        return number_of_customers_rated;
    }

    public void setNumber_of_customers_rated(Integer number_of_customers_rated) {
        this.number_of_customers_rated = number_of_customers_rated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "RestaurantEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", restaurant_name='" + restaurant_name + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", customer_rating=" + customer_rating +
                ", average_price_for_two=" + average_price_for_two +
                ", number_of_customers_rated=" + number_of_customers_rated +
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
