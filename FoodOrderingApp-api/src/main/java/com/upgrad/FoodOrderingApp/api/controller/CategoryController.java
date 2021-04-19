package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
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


    @RequestMapping(path = "/category", method = RequestMethod.GET, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryDetailsResponse>> getAllCategories() {

        List<CategoryEntity> allCategories = categoryBusinessService.getAllCategories();
        List<CategoryDetailsResponse> allCategoriesResoponse =
                new ArrayList<CategoryDetailsResponse>();

        for (int i = 0; i < allCategories.size(); i++) {
            CategoryDetailsResponse category =
                    new CategoryDetailsResponse().id(UUID.fromString(allCategories.get(i).getUuid()))
                            .categoryName(allCategories.get(i).getCategory_name());

            allCategoriesResoponse.add(category);

        }

        return new ResponseEntity<List<CategoryDetailsResponse>>( allCategoriesResoponse,
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse>findCategoryById(@PathVariable("category_id")final String category_uuid) {
//        if (category_uuid == "" || category_uuid == null) {
//            throw CategoryNotFoundException("CNF-001", "Category id field should not be empty");
//        }

        CategoryEntity category = categoryBusinessService.getCategory(category_uuid);

//        if (category == null) {
//            throw CategoryNotFoundException("CNF-002", "No category by this id");
//        }

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

            categoryDetails.addItemListItem(itemList);
        }


        return new ResponseEntity<CategoryDetailsResponse>( categoryDetails, HttpStatus.OK);
    }
}
