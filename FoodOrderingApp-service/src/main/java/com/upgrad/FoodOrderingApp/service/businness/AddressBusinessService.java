package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressBusinessService {

    @Autowired
    private AddressDao addressDao;

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
//        if (addressEntity == null) {
//            throw new AddressNotFoundException("ANF-003","No address by this id");
//        }
        CustomerEntity loggedInCustomer = customerAuthEntity.getCustomer();
        //if (loggedInCustomer.getUuid() != customerAddressEntity.getCustomer)
        // customer auth table needed to be updated

        addressDao.deleteAddress(addressEntity, addressId);
        return addressEntity;
    }
}
