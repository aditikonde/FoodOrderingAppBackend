package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;


    public List<CategoryEntity> getAllCategories() {
        try {
            return entityManager.createNamedQuery("getAllCategories", CategoryEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CategoryEntity getCategoryByUUid(final String Uuid) {
        try {
            return entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class).setParameter(
                    "uuid", Uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<CategoryItemEntity> getAllItemsForCategory(final Integer category_id) {
        try {
            return entityManager.createNamedQuery("getOneCategory", CategoryItemEntity.class).setParameter("id", category_id).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CategoryEntity getCategoryById(final Integer id) {
        try {
            return entityManager.createNamedQuery("getCategoryById", CategoryEntity.class).setParameter(
                    "id", id).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
