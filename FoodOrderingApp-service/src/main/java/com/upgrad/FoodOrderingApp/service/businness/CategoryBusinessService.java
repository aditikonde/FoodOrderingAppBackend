package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryBusinessService {

    @Autowired
    private CategoryDao categoryDao;

    @Transactional
    public List<CategoryEntity> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    @Transactional
    public CategoryEntity getCategory(String uuid) { return categoryDao.getCategoryByUUid(uuid);}

    @Transactional
    public List<CategoryItemEntity> getItemsForCategory(Integer category_id) {
        return categoryDao.getAllItemsForCategory(category_id);
    }
}
