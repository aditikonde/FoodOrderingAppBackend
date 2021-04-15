package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestBody(required = false) final SaveAddressRequest saveAddressRequest,@RequestHeader("authorization") final String authorization)
    {
        CustomerAuthEntity customerAuthToken = customerBusinessService.getCustomerByAuthToken(authorization);
        if(customerAuthToken == null)
            return null;
        else
        {
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
}
