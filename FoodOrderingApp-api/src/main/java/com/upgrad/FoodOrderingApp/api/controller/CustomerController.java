package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerBusinessService customerBusinessService;

    /*
        This endpoint is used to register a new customer in the FoodOrdering Application.
        On successful registration, the information is stored in the database and JSON response
        is created with appropriate message and HTTP status.
     */

    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes =
            MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupUserRequest) throws SignUpRestrictedException {
        final CustomerEntity userEntity = new CustomerEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("1234abc");

        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final CustomerEntity createdUserEntity = customerBusinessService.signup(userEntity);
        SignupCustomerResponse userResponse =
                new SignupCustomerResponse().id(createdUserEntity.getUuid())
                        .status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(userResponse, HttpStatus.CREATED);
    }


    /*
       This endpoint is used for customer authentication. The customer logins in the application
       and after successful authentication, access token is given to a customer.
    */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login",consumes =
            MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArr = decodedText.split(":");
        CustomerAuthEntity customerAuthToken = customerBusinessService.authenticate(decodedArr[0],
                decodedArr[1]);
        CustomerEntity customer = customerAuthToken.getCustomer();
        LoginResponse response =
                new LoginResponse().id(UUID.fromString(customer.getUuid()).toString()).message("SIGNED IN " +
                        "SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token",customerAuthToken.getAccess_token());
        return new ResponseEntity<LoginResponse>(response,headers,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> signout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerBusinessService.getCustomerByAuthToken(authorization);
        if(customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        if(customerAuthEntity != null && customerAuthEntity.getLogout_at() != null && customerAuthEntity.getLogout_at().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        if(customerAuthEntity != null && customerAuthEntity.getExpires_at().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");

        customerAuthEntity.setLogout_at(ZonedDateTime.now());
        CustomerEntity customer = customerAuthEntity.getCustomer();
        customerBusinessService.updateCustomerAuthEntity(customerAuthEntity);
        LogoutResponse response = new LogoutResponse().id(UUID.fromString(customer.getUuid()).toString()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(response,HttpStatus.OK);
    }

    /*
        This endpoint is used to update the customer. Only the customer himself can update.
     */

    @RequestMapping(method = RequestMethod.PUT, path = "/customer",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(

            @RequestHeader("authorization") final String authorization,
            final UpdateCustomerRequest updateCustomerRequest) throws AuthorizationFailedException, UpdateCustomerException {

        final CustomerEntity updateCustomer = new CustomerEntity();
        updateCustomer.setFirstName(updateCustomerRequest.getFirstName());
        updateCustomer.setLastName(updateCustomerRequest.getLastName());

        CustomerEntity updatedCustomer = customerBusinessService.updateCustomer(authorization,updateCustomer);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(UUID
                .fromString(updatedCustomer.getUuid()).toString()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

}
