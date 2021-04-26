package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryBusinessService categoryBusinessService;

    /*
        This endpoint is used to fetch all the categories.
        Any user can access this endpoint.
     */
    @RequestMapping(path = "/category", method = RequestMethod.GET, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {

        List<CategoryEntity> allCategories = categoryBusinessService.getAllCategories();

        List<CategoryListResponse> list = new ArrayList<CategoryListResponse>();

        for(int i = 0; i < allCategories.size(); i++) {
            CategoryListResponse category = new CategoryListResponse();
            category.id(UUID.fromString(allCategories.get(i).getUuid()))
                    .categoryName(allCategories.get(i).getCategory_name());

            list.add(category);
        }
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        categoriesListResponse.categories(list);
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }


    /*
        This endpoint is used to fetch a particular category.
        Any user can access this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse>getCategoryById(@PathVariable("category_id")final String category_uuid) throws CategoryNotFoundException {

        if (category_uuid.isEmpty() || category_uuid == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity category = categoryBusinessService.getCategory(category_uuid);

        if (category == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }



        CategoryDetailsResponse categoryDetails = new CategoryDetailsResponse();
        categoryDetails.categoryName(category.getCategory_name());
        categoryDetails.id(UUID.fromString(category.getUuid().toString()));

        List<CategoryItemEntity> itemListForCategory =
                categoryBusinessService.getItemsForCategory(category.getId());


        for(int i = 0; i < itemListForCategory.size(); i++) {
            ItemList itemList = new ItemList();

            String val = "VEG";
            if(itemListForCategory.get(i).getItem().getType().equalsIgnoreCase("1")) {
                val = "NON_VEG";
            }

            itemList.id(UUID.fromString(itemListForCategory.get(i).getItem().getUuid()))
                    .itemName(itemListForCategory.get(i).getItem().getItem_name())
                    .price(itemListForCategory.get(i).getItem().getPrice())
                    .itemType(ItemList.ItemTypeEnum.valueOf(val));
            //.itemType(itemListForCategory.get(i).getItem().getType())

            categoryDetails.addItemListItem(itemList);
        }


        return new ResponseEntity<CategoryDetailsResponse>( categoryDetails, HttpStatus.OK);
    }
}
