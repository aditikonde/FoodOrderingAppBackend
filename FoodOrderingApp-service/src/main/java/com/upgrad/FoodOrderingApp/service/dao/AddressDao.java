package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AddressEntity saveAddress(AddressEntity newAddress) {
        entityManager.persist(newAddress);
        return newAddress;
    }

    public StateEntity getStateByUuid(String state_uuid) {
        try {
            return entityManager.createQuery("select u from StateEntity u where u.uuid = " +
                    ":state_uuid", StateEntity.class).setParameter("state_uuid",state_uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AddressEntity getAddressByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("getAddressByUuid", AddressEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            System.out.println("Inside catch block");
            return null;
        }
    }

    public void deleteAddress(AddressEntity addressEntity, String addressId) {
        entityManager.remove(addressEntity);
        entityManager.createNamedQuery("deleteAddress").setParameter(
                "uuid", addressId);
    }

    public CustomerAddressEntity createCustomerAddressEntity(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    public AddressEntity getAddressById(Integer address_id) {
        try {
            return entityManager.createQuery("select u from AddressEntity u where u.id = " +
                    ":address_id", AddressEntity.class).setParameter("address_id",address_id).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AddressEntity> getAllSavedAddressByCustomer(final Integer id) {
        try {
            return entityManager.createNamedQuery("allSavedAddressByCustomerId", AddressEntity.class).setParameter("addid", id).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
