package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
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

    @Autowired
    private CustomerBusinessService customerBusinessService;

    @Transactional
    public CouponEntity getCouponByName(String coupon_name) {
        return orderDao.getCouponByName(coupon_name);
    }

    @Transactional
    public List<OrdersEntity> getCustomerOrders(final CustomerEntity customerEntity, final String authorizationToken) throws AuthorizationFailedException {


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
