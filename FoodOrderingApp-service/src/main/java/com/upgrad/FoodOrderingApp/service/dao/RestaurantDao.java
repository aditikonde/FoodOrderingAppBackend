package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants() {

        try {
            return entityManager.createNamedQuery("allRestaurants", RestaurantEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity getRestaurantByUUId(String restaurantUUID) {
        try {
            return entityManager.createNamedQuery("findRestaurantByUUId", RestaurantEntity.class).setParameter("restaurantUUID",restaurantUUID.toLowerCase()).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public RestaurantEntity getRestaurantById(Integer restaurantID) {
        try {
            return entityManager.createNamedQuery("findRestaurantById", RestaurantEntity.class).setParameter("restaurantID",restaurantID).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public void updateRestaurant(final RestaurantEntity updatedRestaurantEntity) {
        entityManager.merge(updatedRestaurantEntity);
    }

    public List<RestaurantCategoryEntity> getAllCategoriesByRestaurantId(RestaurantEntity restaurant) {

        try {
            return entityManager.createNamedQuery("allRestaurantCategoriesByRestaurantId", RestaurantCategoryEntity.class).setParameter("restaurant",restaurant).getResultList();
        } catch(NoResultException nre) {
            return null;
        }

    }

    public List<RestaurantCategoryEntity> getAllCategoriesByCategoryId(CategoryEntity category) {

        try {
            return entityManager.createNamedQuery("allRestaurantCategoriesByCategoryId", RestaurantCategoryEntity.class).setParameter("category",category).getResultList();
        } catch(NoResultException nre) {
            return null;
        }

    }

    public RestaurantEntity getRestaurantByName(String name) {
        try {
            return entityManager.createNamedQuery("findRestaurantByName", RestaurantEntity.class).setParameter("name",name.toLowerCase()).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
