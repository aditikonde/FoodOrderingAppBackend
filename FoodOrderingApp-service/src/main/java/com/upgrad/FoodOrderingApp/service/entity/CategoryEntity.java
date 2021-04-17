package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CATEGORY")
@NamedQueries({
        @NamedQuery(name = "getAllCategories", query = "select c from CategoryEntity c"),
        @NamedQuery(name = "getCategoryByUuid", query = "select q from CategoryEntity q where q" +
                ".uuid = :uuid")

})
public class CategoryEntity {

    public CategoryEntity(){}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 64)
    private String uuid;

    @Column(name = "category_name")
    @Size(max = 64)
    private String category_name;

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

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", category_name='" + category_name + '\'' +
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
