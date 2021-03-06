package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerEntity createCustomer(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    public CustomerAuthEntity createAuthToken(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

    public CustomerAuthEntity updateCustomerAuthEntity(final CustomerAuthEntity customerAuthEntity) {
        entityManager.merge(customerAuthEntity);
        return customerAuthEntity;
    }

    public CustomerAuthEntity getCustomerByAccessToken(String accessToken) {
        try {
            return entityManager.createQuery("select u from CustomerAuthEntity u where u.access_token = " +
                    ":accessToken",CustomerAuthEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity getCustomerByContactNumber(String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNum", CustomerEntity.class).setParameter(
                    "contactNumber", contactNumber).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity updateCustomer(final CustomerEntity customer) {

        return entityManager.merge(customer);
    }

    public CustomerEntity getCustomerByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("customerByUuid", CustomerEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }


    public CustomerEntity getCustomerById(final Integer id) {
        try {
            return entityManager.createNamedQuery("customerById", CustomerEntity.class).setParameter("id", id)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public CustomerAddressEntity getCustAddressByAddressId(CustomerEntity customer, AddressEntity address) {

        try {
            return entityManager.createNamedQuery("custAddressByCustIdAddressId", CustomerAddressEntity.class)
                    .setParameter("customer",customer)
                    .setParameter("address", address)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public List<CustomerAddressEntity> getAddressByCustomer(final Integer id) {
        try {
            return entityManager.createNamedQuery("getAddressesByCustomerId", CustomerAddressEntity.class)
                    .setParameter("id", id)
                    .getResultList();
        } catch(NoResultException nre) {
            return null;
        }
    }


}
