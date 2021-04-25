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

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantByRestaurantId(@PathVariable("restaurant_id") final String restaurant_id) {

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

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();
        restaurantDetailsResponse.id(UUID.fromString(restaurantEntity.getUuid()))
                                    .restaurantName(restaurantEntity.getRestaurant_name())
                                    .photoURL(restaurantEntity.getPhoto_url())
                                    .customerRating(restaurantEntity.getCustomer_rating())
                                    .averagePrice(restaurantEntity.getAverage_price_for_two())
                                    .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                                    .address(address);

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
