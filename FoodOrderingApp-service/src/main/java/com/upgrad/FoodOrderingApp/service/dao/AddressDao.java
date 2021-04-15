package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

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
}
