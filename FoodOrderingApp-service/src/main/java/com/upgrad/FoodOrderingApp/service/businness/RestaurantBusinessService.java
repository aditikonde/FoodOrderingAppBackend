package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class RestaurantBusinessService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerBusinessService customerBusinessService;

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
    }

    @Transactional
    public RestaurantEntity updateCustomerRating (final Double customerRating, final String restaurant_id, final String authorizationToken)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerByAccessToken(authorizationToken);
        if (customerAuthEntity == null) {
            //throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        //uncomment when logout endpoint is done

//        final ZonedDateTime now = ZonedDateTime.now();
//        final ZonedDateTime loggedOutTime = customerAuthEntity.getLogoutAt();
//        final long difference = now.compareTo(loggedOutTime);
//        if (difference > 0) {
//            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
//        }

        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expireTime = customerAuthEntity.getExpires_at();
        final long difference = now.compareTo(expireTime);

        if (difference > 0) {
            //throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        customerBusinessService.getCustomerByAuthToken(authorizationToken);


        if(restaurant_id == null || restaurant_id.isEmpty() || restaurant_id.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        //get the restaurant Details using the restaurantUuid
        RestaurantEntity restaurantEntity =  restaurantDao.getRestaurantByUUId(restaurant_id);

        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        // Throw exception if path variable(restaurant_id) is empty
        if(customerRating == null || customerRating < 1 || customerRating > 5 ){
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        // Now calculate new customer rating  and set the updated rating and attach it to the restaurantEntity
        BigDecimal oldRatingCalculation = (restaurantEntity.getCustomer_rating().multiply(new BigDecimal(restaurantEntity.getNumber_of_customers_rated())));
        BigDecimal calculatedRating = (oldRatingCalculation.add(new BigDecimal(customerRating))).divide(new BigDecimal(restaurantEntity.getNumber_of_customers_rated() + 1));
        restaurantEntity.setCustomer_rating(calculatedRating);
        restaurantEntity.setNumber_of_customers_rated(restaurantEntity.getNumber_of_customers_rated() + 1);

        //called restaurantDao to merge the content and update in the database
        restaurantDao.updateRestaurant(restaurantEntity);
        return restaurantEntity;
    }

    public RestaurantEntity getRestaurantByUUID(String restaurantUUID) {
        return restaurantDao.getRestaurantByUUId(restaurantUUID);
    }


}
