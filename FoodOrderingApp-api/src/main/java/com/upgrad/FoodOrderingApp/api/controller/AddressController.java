package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    private CustomerBusinessService customerBusinessService;

    @Autowired
    private AddressBusinessService addressBusinessService;

    @RequestMapping(method = RequestMethod.POST,path = "/address",consumes =
            MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestBody(required = false) final SaveAddressRequest saveAddressRequest,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, SaveAddressException {
        CustomerAuthEntity customerAuthToken = customerBusinessService.getCustomerByAuthToken(authorization);
        /*if(customerAuthToken == null)
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        if(customerAuthToken != null && customerAuthToken.getLogout_at() != null && customerAuthToken.getLogout_at().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        if(customerAuthToken != null && customerAuthToken.getExpires_at().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        if(saveAddressRequest.getFlatBuildingName().equals("") || saveAddressRequest.getLocality().equals("")||
        saveAddressRequest.getCity().equals("") || saveAddressRequest.getPincode().equals("") ||
        saveAddressRequest.getStateUuid().equals(""))
            throw new SaveAddressException("SAR-001","No field can be empty");
        */
            AddressEntity newAddress = new AddressEntity();
            StateEntity stateEntity = addressBusinessService.getStateByUuid(saveAddressRequest.getStateUuid().toString());
            newAddress.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
            newAddress.setLocality(saveAddressRequest.getLocality());
            newAddress.setCity(saveAddressRequest.getCity());
            newAddress.setPincode(saveAddressRequest.getPincode());
            newAddress.setState(stateEntity);
            newAddress.setUuid(UUID.randomUUID().toString());
            addressBusinessService.saveAddress(newAddress);
            SaveAddressResponse addressResponse = new SaveAddressResponse().
                    id(UUID.fromString(newAddress.getUuid()).toString()).status("ADDRESS SUCCESSFULLY SAVED");
            return new ResponseEntity<SaveAddressResponse>(addressResponse, HttpStatus.CREATED);
    }
}
