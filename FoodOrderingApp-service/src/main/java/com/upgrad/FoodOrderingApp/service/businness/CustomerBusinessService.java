package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
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

import java.util.regex.Pattern;

import java.time.ZonedDateTime;

//import java.util.UUID;
import java.util.*;

import java.util.List;
import java.util.UUID;


@Service
public class CustomerBusinessService {
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerBusinessService customerBusinessService;
    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {
//        if(customerEntity.getFirstName()==null || customerEntity.getPassword()==null || customerEntity.getEmail()==null
//                || customerEntity.getFirstName()=="" || customerEntity.getPassword()=="" || customerEntity.getEmail().equals("")) {
//            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be " +
//                    "filled");
//        }

        if(!isEmailValid( customerEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        if(!isContactValid( customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        if(!isPasswordValid( customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }



        CustomerEntity userEntity1 =
                customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());


        if (userEntity1 != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already " +
                    "registered! Try other contact number.");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        return customerDao.createCustomer(customerEntity);
    }

    // helper method to validate email
    public boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    // helper method to validate contact
    public boolean isContactValid(String contact) {
        Pattern pattern = Pattern.compile("^\\d{10}$");
        return pattern.matcher(contact).matches();
    }

    // helper method to validate password
    public boolean isPasswordValid(String password)
    {
        // Checking lower alphabet in string
        int n = password.length();
        boolean hasLower = false, hasUpper = false,
                hasDigit = false, specialChar = false;

        Set<Character> set = new HashSet<Character>(
                Arrays.asList('#', '@', '$', '%', '&', '^',
                        '*', '!'));
        for (char i : password.toCharArray())
        {
            if (Character.isLowerCase(i))
                hasLower = true;
            if (Character.isUpperCase(i))
                hasUpper = true;
            if (Character.isDigit(i))
                hasDigit = true;
            if (set.contains(i))
                specialChar = true;
        }

        System.out.print("Strength of password:- ");
        if (hasDigit && hasLower && hasUpper && specialChar && (n >= 8)) {
            return true;
        }
        return false;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactNumber, final String password) throws AuthenticationFailedException {
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
            throw new AuthenticationFailedException("ATH-002","Invalid Credentials");
        }

    }

    public CustomerAuthEntity getCustomerByAuthToken(String access_token) {

        String [] bearerToken = access_token.split("Bearer ");
        CustomerAuthEntity customerAuthEntity =
                customerDao.getCustomerByAccessToken(bearerToken[1]);

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity == null) {
            //throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");

        } else if (customerAuthEntity.getLogout_at() != null) {
            // throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");

        } else if (now.isAfter(customerAuthEntity.getExpires_at()) ) {
            //throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }


        if(customerAuthEntity != null ) {
            return customerAuthEntity;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity updateCustomerAuthEntity(final String authorization) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerBusinessService.getCustomerByAuthToken(authorization);

//        if(customerAuthEntity == null)
//            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
//        if(customerAuthEntity != null && customerAuthEntity.getLogout_at() != null && customerAuthEntity.getLogout_at().isBefore(ZonedDateTime.now()))
//            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
//        if(customerAuthEntity != null && customerAuthEntity.getExpires_at().isBefore(ZonedDateTime.now()))
//            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");

        customerAuthEntity.setLogout_at(ZonedDateTime.now());
        CustomerEntity customer = customerAuthEntity.getCustomer();
        return customerDao.updateCustomerAuthEntity(customerAuthEntity);
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

    public CustomerAddressEntity getCustAddressByAddressId(CustomerEntity customer, AddressEntity address) {
        return customerDao.getCustAddressByAddressId(customer,address);
    }

    public List<CustomerAddressEntity> getAddressByCustomer(final Integer id) {
        return customerDao.getAddressByCustomer(id);
    }

    public CustomerEntity getCustomer(String access_token) throws AuthorizationFailedException {

        String [] bearerToken = access_token.split("Bearer ");
        CustomerAuthEntity customerAuthEntity =
                customerDao.getCustomerByAccessToken(bearerToken[1]);

        final ZonedDateTime now = ZonedDateTime.now();

         if (customerAuthEntity.getLogout_at() != null)
             throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        if (now.isAfter(customerAuthEntity.getExpires_at()) )
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        if (customerAuthEntity == null)
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");

        if(customerAuthEntity != null ) {
            return customerAuthEntity.getCustomer();
        }
        return null;
    }

    public CustomerEntity getCustomerById(Integer id) {
        return customerDao.getCustomerById(id);
    }

}
