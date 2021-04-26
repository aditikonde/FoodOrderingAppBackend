package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.ZonedDateTime;

import java.util.List;

@Service
public class AddressBusinessService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerBusinessService customerBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity newAddress) {
        return addressDao.saveAddress(newAddress);
    }

    public StateEntity getStateByUuid(String state_uuid) {
        return addressDao.getStateByUuid(state_uuid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(String addressId, CustomerAuthEntity customerAuthEntity) throws
            AuthorizationFailedException, AddressNotFoundException {

        AddressEntity addressEntity =addressDao.getAddressByUuid(addressId);
        if (addressEntity == null) {
            throw new AddressNotFoundException("ANF-003","No address by this id");
        }
        CustomerEntity loggedInCustomer = customerAuthEntity.getCustomer();
        //if (loggedInCustomer.getUuid() != customerAddressEntity.getCustomer)
        // customer auth table needed to be updated

        addressDao.deleteAddress(addressEntity, addressId);
        return addressEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity createCustomerAddressEntity(CustomerAddressEntity customerAddressEntity) {
        return addressDao.createCustomerAddressEntity(customerAddressEntity);
    }


    public AddressEntity getAddressById(Integer address_id) {
        return addressDao.getAddressById(address_id);
    }


    /*
       This service is used to fetch all the questions posed by a specific user.
       Any user can access this endpoint.
    */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AddressEntity> getAllSavedAddressByCustomer(final String authorizationToken) throws AuthorizationFailedException
    {

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

        //CustomerEntity customerEntity = customerDao.();


        return addressDao.getAllSavedAddressByCustomer(customerAuthEntity.getCustomer().getId());
    }


    @Transactional
    public List<StateEntity> getAllStates() {
        return addressDao.getAllStates();
    }

    public AddressEntity getAddressByUUID(String addressId) {
        return addressDao.getAddressByUuid(addressId);
    }

}
