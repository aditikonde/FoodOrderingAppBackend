package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

        CustomerAuthEntity customerAuthToken =
                customerBusinessService.getCustomerByAuthToken(authorization);
        if(customerAuthToken == null)
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        if(customerAuthToken != null && customerAuthToken.getLogout_at() != null && customerAuthToken.getLogout_at().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        if(customerAuthToken != null && customerAuthToken.getExpires_at().isBefore(ZonedDateTime.now()))
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        if(saveAddressRequest.getFlatBuildingName().equals("") || saveAddressRequest.getLocality().equals("")||
                saveAddressRequest.getCity().equals("") || saveAddressRequest.getPincode().equals("") ||
                saveAddressRequest.getStateUuid().equals(""))
            throw new SaveAddressException("SAR-001","No field can be empty");
        if(saveAddressRequest.getPincode().length() != 6 || !saveAddressRequest.getPincode().matches("[0-9]+"))
            throw new SaveAddressException("SAR-002","Invalid pincode");
        AddressEntity newAddress = new AddressEntity();
        StateEntity stateEntity = addressBusinessService.getStateByUuid(saveAddressRequest.getStateUuid().toString());
        if(stateEntity == null)
            throw new SaveAddressException("ANF-002","No state by this id");
        newAddress.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
        newAddress.setLocality(saveAddressRequest.getLocality());
        newAddress.setCity(saveAddressRequest.getCity());
        newAddress.setPincode(saveAddressRequest.getPincode());
        newAddress.setState(stateEntity);
        newAddress.setUuid(UUID.randomUUID().toString());
        AddressEntity savedNewAddress = addressBusinessService.saveAddress(newAddress);
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(savedNewAddress);
        customerAddressEntity.setCustomer(customerAuthToken.getCustomer());
        addressBusinessService.createCustomerAddressEntity(customerAddressEntity);
        SaveAddressResponse addressResponse = new SaveAddressResponse().
                id(UUID.fromString(newAddress.getUuid()).toString()).status("ADDRESS SUCCESSFULLY SAVED");
        return new ResponseEntity<SaveAddressResponse>(addressResponse, HttpStatus.CREATED);
    }


    /*
        This endpoint is used to delete an address that has been saved by a customer. Only the owner
        of the address can delete the address.
    */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{addressId}", produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity <DeleteAddressResponse> deleteAddress(@PathVariable("addressId")
            final String addressId, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AddressNotFoundException {
        if (addressId == "" || addressId == null){
            throw new AddressNotFoundException("ANF-005","Address id can not be empty");
        }


        CustomerAuthEntity customerAuthEntity =
                customerBusinessService.getCustomerByAuthToken(authorization);

//        if (customerAuthEntity == null) {
//            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
//        }
//        ZonedDateTime now = ZonedDateTime.now();
//        if (customerAuthEntity.getLogout_at().isBefore(now)) {
//            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again " +
//                    "to access this endpoint.");
//        }
//
//        if (customerAuthEntity.getExpires_at().isBefore(now)) {
//            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again" +
//                    " to access this endpoint.");
//        }


        final AddressEntity addressEntity = addressBusinessService.deleteAddress(addressId,
                customerAuthEntity);
        DeleteAddressResponse deleteAddressResponse =
                new DeleteAddressResponse().id(UUID.fromString(addressId)).status("ADDRESS " +
                        "DELETED SUCCESSFULLY");
        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);
    }

    /*

       This endpoint is used to fetch all the saved address for a customer.
    */
    @RequestMapping(method = RequestMethod.GET,path="/address/customer",produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddressForCustomer(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        List<AddressEntity> allSavedAddress = addressBusinessService.getAllSavedAddressByCustomer(authorization);

        AddressListResponse allSavedAddressResponses = new AddressListResponse();

        List<AddressList> addressList = new ArrayList<AddressList>();

        for (int i = 0; i < allSavedAddress.size(); i++) {
            AddressList addr = new AddressList();
            AddressListState addressListState = new AddressListState();

            addr.setId(UUID.fromString(allSavedAddress.get(i).getUuid()));
            addr.setFlatBuildingName(allSavedAddress.get(i).getFlat_buil_number());
            addr.setLocality(allSavedAddress.get(i).getLocality());
            addr.setCity(allSavedAddress.get(i).getCity());
            addr.setPincode(allSavedAddress.get(i).getPincode());
            addressListState.setId(UUID.fromString(allSavedAddress.get(i).getState().getUuid()));
            addressListState.setStateName(allSavedAddress.get(i).getState().getState_name());

            addressList.add(addr);
            addr.setState(addressListState);

        }
        allSavedAddressResponses.setAddresses(addressList);

        return new ResponseEntity<AddressListResponse>(allSavedAddressResponses, HttpStatus.OK);
    }

    /*
        This endpoint is used to fetch all the states.
        Any user can access this endpoint.
     */
    @RequestMapping(path = "/states", method = RequestMethod.GET, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {

        List<StateEntity> listOfStates = addressBusinessService.getAllStates();

        List<StatesList> list = new ArrayList<StatesList>();

        for (int i = 0; i < listOfStates.size(); i++) {
            StatesList state = new StatesList();
            state.id(UUID.fromString(listOfStates.get(i).getUuid()))
                    .stateName(listOfStates.get(i).getState_name());

            list.add(state);
        }
        StatesListResponse statesListResponse = new StatesListResponse();
        statesListResponse.states(list);

        return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);

    }
}
