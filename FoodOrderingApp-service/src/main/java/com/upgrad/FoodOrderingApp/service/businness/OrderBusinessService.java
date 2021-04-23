package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrderBusinessService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Transactional
    public CouponEntity getCouponByName(String coupon_name) {
        return orderDao.getCouponByName(coupon_name);
    }

    @Transactional
    public List<OrdersEntity> getCustomerOrders(final CustomerEntity customerEntity, final String authorizationToken) {

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

        return orderDao.getCustomerOrders(customerEntity);
    }

    public CouponEntity getCouponByCouponUUID(String couponId) {
        return orderDao.getCouponByUUID(couponId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdersEntity saveOrder(OrdersEntity order) {
        return orderDao.saveOrder(order);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrderItem(OrderItemEntity orderItemEntity) {
        orderItemDao.createOrderItemEntity(orderItemEntity);
    }

}
