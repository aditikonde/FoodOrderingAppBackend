package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurant_name) {
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
        AddressEntity addressEntity = addressBusinessService.getAddressById(restaurantEntityList.get(i).getAddress_id());
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

        RestaurantList res = new RestaurantList().address(responseAddress)
                .averagePrice(restaurantEntityList.get(i).getAverage_price_for_two())
                .customerRating(restaurantEntityList.get(i).getCustomer_rating())
                .numberCustomersRated(restaurantEntityList.get(i).getNumber_of_customers_rated())
                .customerRating(restaurantEntityList.get(i).getCustomer_rating())
                .id(UUID.fromString(restaurantEntityList.get(i).getUuid()))
                .photoURL(restaurantEntityList.get(i).getPhoto_url())
                .restaurantName(restaurantEntityList.get(i).getRestaurant_name());
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(@PathVariable("category_id") final String category_id) {
        List<RestaurantEntity> restaurantEntityList = restaurantBusinessService.getAllRestaurants();
        List<RestaurantList> list = new ArrayList<>();
        for (int i = 0; i < restaurantEntityList.size(); i++) {
            if (restaurantEntityList.get(i).getUuid().contains(category_id)) {
                list.add(getRestaurantDetails(restaurantEntityList,i));
            }
        }
        RestaurantListResponse response = new RestaurantListResponse().restaurants(list);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByRestaurantId(@PathVariable("restaurant_id") final String restaurant_id) {
        List<RestaurantEntity> restaurantEntityList = restaurantBusinessService.getAllRestaurants();
        List<RestaurantList> list = new ArrayList<>();
        for (int i = 0; i < restaurantEntityList.size(); i++) {
            if (restaurantEntityList.get(i).getUuid().contains(restaurant_id)) {
                list.add(getRestaurantDetails(restaurantEntityList,i));
            }
        }
        RestaurantListResponse response = new RestaurantListResponse().restaurants(list);
        return new ResponseEntity<RestaurantListResponse>(response, HttpStatus.OK);
    }

}