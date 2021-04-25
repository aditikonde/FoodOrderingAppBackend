package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantBusinessService restaurantBusinessService;

    @Autowired
    private AddressBusinessService addressBusinessService;

    @Autowired
    private CategoryBusinessService categoryBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants()
    {
        List<RestaurantEntity> restaurantEntityList = restaurantBusinessService.getAllRestaurants();
        List<RestaurantList> list = new ArrayList<>();
        for (int i = 0; i < restaurantEntityList.size(); i++) {
            list.add(getRestaurantDetails(restaurantEntityList,i));
        }
        RestaurantListResponse response = new RestaurantListResponse().restaurants(list);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurant_name) throws RestaurantNotFoundException {
        if(restaurant_name.equals(""))
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");
        List<RestaurantEntity> restaurantEntityList = restaurantBusinessService.getAllRestaurants();
        List<RestaurantList> list = new ArrayList<>();
        for (int i = 0; i < restaurantEntityList.size(); i++) {
            if (restaurantEntityList.get(i).getRestaurant_name().toLowerCase().contains(restaurant_name.toLowerCase())) {
                list.add(getRestaurantDetails(restaurantEntityList,i));
            }
        }
        RestaurantListResponse response = new RestaurantListResponse().restaurants(list);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

    private RestaurantList getRestaurantDetails(List<RestaurantEntity> restaurantEntityList, int i)
    {
        AddressEntity addressEntity = addressBusinessService.getAddressById(restaurantEntityList.get(i).getAddress().getId());
        StateEntity stateEntity = addressBusinessService.getStateByUuid(addressEntity.getState().getUuid());
        RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState().stateName(stateEntity.getState_name())
                .id(UUID.fromString(stateEntity.getUuid()));

        RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress()
                .city(addressEntity.getCity())
                .flatBuildingName(addressEntity.getFlat_buil_number())
                .id(UUID.fromString(addressEntity.getUuid()))
                .locality(addressEntity.getLocality())
                .pincode(addressEntity.getPincode())
                .state(responseAddressState);

        String categories = categoryBusinessService.getRestaurantCategoriesByRestaurantID(restaurantEntityList.get(i).getId());
        RestaurantList res = new RestaurantList().address(responseAddress)
                .averagePrice(restaurantEntityList.get(i).getAverage_price_for_two())
                .customerRating(restaurantEntityList.get(i).getCustomer_rating())
                .numberCustomersRated(restaurantEntityList.get(i).getNumber_of_customers_rated())
                .customerRating(restaurantEntityList.get(i).getCustomer_rating())
                .id(UUID.fromString(restaurantEntityList.get(i).getUuid()))
                .photoURL(restaurantEntityList.get(i).getPhoto_url())
                .restaurantName(restaurantEntityList.get(i).getRestaurant_name())
                .categories(categories);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(@PathVariable("category_id") final String category_Uuid) {

        CategoryEntity categoryEntity = categoryBusinessService.getCategory(category_Uuid);
        List<RestaurantCategoryEntity> restaurantCategoryEntityList = restaurantBusinessService.getRestaurantCategoryByCategoryId(categoryEntity);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        List<RestaurantList> restaurantLists = new ArrayList<RestaurantList>();
        for(RestaurantCategoryEntity restaurantCategoryEntity: restaurantCategoryEntityList){

            RestaurantList restaurantList = new RestaurantList();
            //RestaurantEntity restaurantEntity = new RestaurantEntity();
            restaurantList.setId(UUID.fromString(restaurantCategoryEntity.getRestaurant().getUuid()));
            restaurantList.restaurantName(restaurantCategoryEntity.getRestaurant().getRestaurant_name());
            restaurantList.setPhotoURL(restaurantCategoryEntity.getRestaurant().getPhoto_url());
            restaurantList.setCustomerRating(restaurantCategoryEntity.getRestaurant().getCustomer_rating());
            restaurantList.setAveragePrice(restaurantCategoryEntity.getRestaurant().getAverage_price_for_two());
            restaurantList.setNumberCustomersRated(restaurantCategoryEntity.getRestaurant().getNumber_of_customers_rated());

            RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress();
            AddressEntity addressEntity  = restaurantCategoryEntity.getRestaurant().getAddress();

            RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState();
            StateEntity stateEntity = addressBusinessService.getStateByUuid(addressEntity.getUuid());
            state.id(UUID.fromString(stateEntity.getUuid()))
                    .stateName(stateEntity.getState_name());


            address.id(UUID.fromString(addressEntity.getUuid()))
                    .flatBuildingName(addressEntity.getFlat_buil_number())
                    .locality(addressEntity.getLocality())
                    .city(addressEntity.getCity())
                    .pincode(addressEntity.getPincode())
                    .state(state);
            restaurantList.setAddress(address);

            List<RestaurantCategoryEntity> categories = restaurantBusinessService.getRestaurantCategoryByRestaurantId(restaurantCategoryEntity.getRestaurant());

            String categoryNames = "" ;
            for(RestaurantCategoryEntity category : categories){
                categoryNames= categoryNames.concat(category.getCategory().getCategory_name()+",");
                }

            restaurantList.categories(categoryNames);

            restaurantLists.add(restaurantList);

        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/restaurant/{restaurant_id}", produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantId(@PathVariable("restaurant_id") final String restaurant_id) throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity =
                restaurantBusinessService.getRestaurantByUUID(restaurant_id);

        RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress();
        AddressEntity addressEntity  = restaurantEntity.getAddress();

        RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState();
        StateEntity stateEntity = addressBusinessService.getStateByUuid(addressEntity.getUuid());
        state.id(UUID.fromString(stateEntity.getUuid()))
                .stateName(stateEntity.getState_name());


        address.id(UUID.fromString(addressEntity.getUuid()))
                .flatBuildingName(addressEntity.getFlat_buil_number())
                .locality(addressEntity.getLocality())
                .city(addressEntity.getCity())
                .pincode(addressEntity.getPincode())
                .state(state);

        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantBusinessService.getRestaurantCategoryByRestaurantId(restaurantEntity);
        List<CategoryList> categories = new ArrayList<CategoryList>();

        for(RestaurantCategoryEntity restaurantCategoryEntity :restaurantCategoryEntities){

            List<ItemList>  items = new ArrayList<ItemList>();
            List<CategoryItemEntity> categoryItemEntities = categoryBusinessService.getItemsForCategory(restaurantCategoryEntity.getCategory().getId());
            for(CategoryItemEntity categoryItemEntity: categoryItemEntities){
                ItemList itemList = new ItemList();
                String val = "VEG";
                if(categoryItemEntity.getItem().getType().equalsIgnoreCase("1")) {
                    val = "NON_VEG";
                }
                itemList.id(UUID.fromString(categoryItemEntity.getItem().getUuid()))
                        .itemName(categoryItemEntity.getItem().getItem_name())
                        .price(categoryItemEntity.getItem().getPrice())
                        .itemType(ItemList.ItemTypeEnum.valueOf(val));

                items.add(itemList);
            }

            CategoryList categoryList = new CategoryList();
            categoryList.id(UUID.fromString(restaurantCategoryEntity.getCategory().getUuid()))
                    .categoryName(restaurantCategoryEntity.getCategory().getCategory_name())
                    .itemList(items);
            categories.add(categoryList);
        }

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();
        restaurantDetailsResponse.id(UUID.fromString(restaurantEntity.getUuid()))
                                    .restaurantName(restaurantEntity.getRestaurant_name())
                                    .photoURL(restaurantEntity.getPhoto_url())
                                    .customerRating(restaurantEntity.getCustomer_rating())
                                    .averagePrice(restaurantEntity.getAverage_price_for_two())
                                    .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                                    .address(address)
                                    .categories(categories);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse,
                HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/api/restaurant/{restaurant_id}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestHeader("authorization") final String authorization, @RequestParam Double customerRating, @PathVariable String restaurant_id ) throws AuthorizationFailedException, InvalidRatingException, RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantBusinessService.updateCustomerRating(customerRating, restaurant_id,authorization);

        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(restaurantEntity.getUuid())).status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

}
