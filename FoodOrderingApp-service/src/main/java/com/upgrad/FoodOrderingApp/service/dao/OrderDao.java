package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByName(final String name) {
        try {
            return entityManager.createNamedQuery("getCouponByName", CouponEntity.class).setParameter(
                    "coupon_name", name).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<OrdersEntity> getCustomerOrders(CustomerEntity customerEntity) {
        try {
            return entityManager.createNamedQuery("ordersByCustomer", OrdersEntity.class).setParameter("customer", customerEntity)
                    .getResultList();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public CouponEntity getCouponByUUID(String couponId) {
        try {
            return entityManager.createNamedQuery("getCouponByUUID", CouponEntity.class).setParameter(
                    "couponId", couponId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public OrdersEntity saveOrder(OrdersEntity order) {
        entityManager.persist(order);
        return order;
    }

    public List<OrdersEntity> getOrdersByRestaurant(final RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("ordersByRestaurant", OrdersEntity.class)
                    .setParameter("restaurant", restaurantEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
