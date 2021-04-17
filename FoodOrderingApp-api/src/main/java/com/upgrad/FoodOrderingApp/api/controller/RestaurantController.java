package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    //    @RequestMapping(method = RequestMethod.GET,path ="/restaurant",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(method = RequestMethod.POST, path = "/restaurant", consumes =
            MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants()
    {

        List<RestaurantEntity> restaurantEntityList = restaurantBusinessService.getAllRestaurants();
        List<RestaurantList> list = new ArrayList<>();

        for(int i=0;i<restaurantEntityList.size();i++)
        {
            AddressEntity addressEntity = addressBusinessService.getAddressById(restaurantEntityList.get(i).getAddress_id());
            StateEntity stateEntity = addressBusinessService.getStateByUuid(addressEntity.getState().getUuid());
            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState().stateName(stateEntity.getState_name())
                    .id(UUID.fromString(stateEntity.getUuid()));

            RestaurantDetailsResponseAddress responseAddress= new RestaurantDetailsResponseAddress()
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
            list.add(res);
        }
        RestaurantListResponse response = new RestaurantListResponse().restaurants(list);

        return new ResponseEntity<RestaurantListResponse>(response,HttpStatus.OK);
    }

    /*@RequestMapping(method = RequestMethod.GET,path ="/restaurant",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RestaurantDetailsResponse>> getAllRestaurants()
    {
        List<RestaurantDetailsResponse> restaurantListResponses = new ArrayList<>();
        List<RestaurantEntity> restaurantEntityList = restaurantBusinessService.getAllRestaurants();
        for(int i=0;i<restaurantEntityList.size();i++)
        {
            AddressEntity addressEntity = addressBusinessService.getAddressById(restaurantEntityList.get(i).getAddress_id());
            StateEntity stateEntity = addressBusinessService.getStateByUuid(addressEntity.getState().getUuid());
            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState().stateName(stateEntity.getState_name())
                    .id(UUID.fromString(stateEntity.getUuid()));
            RestaurantDetailsResponseAddress responseAddress= new RestaurantDetailsResponseAddress()
                    .city(addressEntity.getCity())
                    .flatBuildingName(addressEntity.getFlat_buil_number())
                    .id(UUID.fromString(addressEntity.getUuid()))
                    .locality(addressEntity.getLocality())
                    .pincode(addressEntity.getPincode())
                    .state(responseAddressState);
            RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse().restaurantName(restaurantEntityList.get(i).getRestaurant_name())
                    .id(UUID.fromString(restaurantEntityList.get(i).getUuid()))
                    .photoURL(restaurantEntityList.get(i).getPhoto_url())
                    .customerRating(restaurantEntityList.get(i).getCustomer_rating())
                    .averagePrice(restaurantEntityList.get(i).getAverage_price_for_two())
                    .numberCustomersRated(restaurantEntityList.get(i).getNumber_of_customers_rated())
                    .address(responseAddress);
//                    .categories(restaurantEntityList.get(i).)
            restaurantListResponses.add(restaurantDetailsResponse);
        }
        return new ResponseEntity<List<RestaurantDetailsResponse>>(restaurantListResponses,HttpStatus.OK);
    }*/
}
