package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class CustomerBusinessService {
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {
        CustomerEntity userEntity1 =
                customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());
        if (userEntity1 != null) {
            throw new SignUpRestrictedException("SGR-001", "TThis contact number is already " +
                    "registered! Try other contact number.");
        }

        if(customerEntity.getFirstName()==null || customerEntity.getPassword()==null || customerEntity.getEmail()==null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be " +
                    "filled");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        return customerDao.createCustomer(customerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactNumber, final String password) throws AuthenticationFailedException {
        System.out.println("Inside Auth");
        final CustomerEntity userEntity = customerDao.getCustomerByContactNumber(contactNumber);
        if(userEntity == null)
            throw new AuthenticationFailedException("ATH-001","This contact number has not been registered!");
        String encryptedPassword = PasswordCryptographyProvider.encrypt(password,userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())) {
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthEntity.setCustomer(userEntity);
            customerAuthEntity.setAccess_token(jwtTokenProvider.generateToken(userEntity.getUuid(), now,
                    expiresAt));
            customerAuthEntity.setLogin_at(now);
            customerAuthEntity.setLogout_at(expiresAt);
            customerAuthEntity.setExpires_at(expiresAt);
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            CustomerAuthEntity createdCustomerAuthToken =
                    customerDao.createAuthToken(customerAuthEntity);
            customerDao.updateCustomer(userEntity);
            return createdCustomerAuthToken;

        }
        else {
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }

    }

    public CustomerAuthEntity getCustomerByAuthToken(String access_token) {

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerByAccessToken(access_token);

        if(customerAuthEntity != null ) {
            return customerAuthEntity;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerAuthEntity(CustomerAuthEntity customerAuthEntity) {
        customerDao.updateCustomerAuthEntity(customerAuthEntity);
    }

    /*
        This service is used to update customer. Only the successful login  can update.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final String authorizationToken, final CustomerEntity updateCustomer
    ) throws AuthorizationFailedException, UpdateCustomerException {
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerByAccessToken(authorizationToken);
        if (customerAuthEntity == null) {
            //throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        CustomerEntity originalCustomer = customerAuthEntity.getCustomer();

        if(originalCustomer.getFirstName()==null){
            //throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        //uncomment when logout endpoint is done

        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime loggedOutTime = customerAuthEntity.getLogout_at();
        final long difference1 = now.compareTo(loggedOutTime);
        if (difference1 > 0) {
            //throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        //final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expireTime = customerAuthEntity.getExpires_at();
        final long difference2 = now.compareTo(expireTime);

        if (difference2 > 0) {
            //throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        updateCustomer.setPassword(originalCustomer.getPassword());
        updateCustomer.setEmail(originalCustomer.getEmail());
        updateCustomer.setUuid(originalCustomer.getUuid());
        updateCustomer.setSalt(originalCustomer.getSalt());
        updateCustomer.setContactNumber(originalCustomer.getContactNumber());
        updateCustomer.setId(originalCustomer.getId());


        return customerDao.updateCustomer(updateCustomer);
    }


    @Transactional
    public CustomerEntity changePassword (final String oldPassword, final String newPassword, final String authorizationToken)
            throws AuthorizationFailedException, UpdateCustomerException {

        final ZonedDateTime now = ZonedDateTime.now();

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerByAccessToken(authorizationToken);

        if (customerAuthEntity == null) {
            //throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");

        } else if (customerAuthEntity.getLogout_at() != null) {
           // throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");

        } else if (now.isAfter(customerAuthEntity.getExpires_at()) ) {
            //throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

       //CustomerEntity customerEntity =  customerDao.getCustomerByUuid(customerAuthEntity.getUuid());

        CustomerEntity customerEntity = customerAuthEntity.getCustomer();

        if (oldPassword == null || newPassword ==  null) {
            //throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());

        if(!encryptedPassword.equals(customerEntity.getPassword())) {
            //throw new UpdateCustomerException("UCR-004", "Incorrect old password!");

        } else if (newPassword.length() < 8
                || !newPassword.matches(".*[0-9]{1,}.*")
                || !newPassword.matches(".*[A-Z]{1,}.*")
                || !newPassword.matches(".*[#@$%&*!^]{1,}.*")) {
            //throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        customerEntity.setPassword(newPassword);

        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        customerDao.updateCustomer(customerEntity);
        return customerEntity;
    }

    public CustomerAddressEntity getCustAddressByAddressId(Integer addressId) {
        return customerDao.getCustAddressByAddressId(addressId);
    }

}
