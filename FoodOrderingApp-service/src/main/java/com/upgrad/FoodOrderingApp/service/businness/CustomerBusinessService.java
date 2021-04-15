package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
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
            customerAuthEntity.setExpires_at(expiresAt);
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
}
