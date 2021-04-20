package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.OrderBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
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
public class OrderController {

    @Autowired
    private OrderBusinessService orderBusinessService;

    @Autowired
    private CustomerBusinessService customerBusinessService;


    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> findCouponByName(@PathVariable("coupon_name")
              final String coupon_name, @RequestHeader("authorization") final String authorization)
                throws AuthorizationFailedException, CouponNotFoundException {

        CustomerAuthEntity customerAuthEntity =
                customerBusinessService.getCustomerByAuthToken(authorization);

//        if (customerAuthEntity == null) {
//            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
//        }
//        ZonedDateTime now = ZonedDateTime.now();
//        if (customerAuthEntity.getLogout_at().isBefore(now)) {
//            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint." +
//                    "to access this endpoint.");
//        }
//        if (customerAuthEntity.getExpires_at().isBefore(now)) {
//            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again" +
//                    " to access this endpoint.");
//        }

//        if(coupon_name == "" || coupon_name == null) {
//            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
//        }
        CouponEntity coupon = orderBusinessService.getCouponByName(coupon_name);
//        if(coupon == null ){
//            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
//        }

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.id(UUID.fromString(coupon.getUuid()))
                .couponName(coupon.getCoupon_name())
                .percent(coupon.getPercent());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);


    }
}
