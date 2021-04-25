package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
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
public class OrderController {

    @Autowired
    private OrderBusinessService orderBusinessService;

    @Autowired
    private CustomerBusinessService customerBusinessService;

    @Autowired
    private ItemBusinessService itemBusinessService;

    @Autowired
    private RestaurantBusinessService restaurantBusinessService;

    @Autowired
    private AddressBusinessService addressBusinessService;

    @Autowired
    private PaymentBusinessService paymentBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> findCouponByName(@PathVariable("coupon_name")
              final String coupon_name, @RequestHeader("authorization") final String authorization)
                throws AuthorizationFailedException, CouponNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        CustomerAuthEntity customerAuthEntity =
                customerBusinessService.getCustomerByAuthToken(bearerToken[1]);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (customerAuthEntity.getLogout_at().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint." +
                    "to access this endpoint.");
        }
        if (customerAuthEntity.getExpires_at().isBefore(now)) {
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again" +
                    " to access this endpoint.");
        }

        if(coupon_name == "" || coupon_name == null) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        CouponEntity coupon = orderBusinessService.getCouponByName(coupon_name);
        if(coupon == null ){
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.id(UUID.fromString(coupon.getUuid()))
                .couponName(coupon.getCoupon_name())
                .percent(coupon.getPercent());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);


    }

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrdersOfUser(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {


        CustomerAuthEntity customerAuthTokenEntity = customerBusinessService.getCustomerByAuthToken(authorization);

        CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();

        final List<OrdersEntity> ordersEntityList = orderBusinessService.getCustomerOrders(customerEntity, authorization);

        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();

        List<OrderList> orderDetailsList = new ArrayList<OrderList>();

        for (OrdersEntity ordersEntity: ordersEntityList) {

            OrderListCustomer orderListCustomer = new OrderListCustomer();
            orderListCustomer.setId(UUID.fromString(ordersEntity.getCustomer().getUuid()));
            orderListCustomer.setFirstName(ordersEntity.getCustomer().getFirstName());
            orderListCustomer.setLastName(ordersEntity.getCustomer().getLastName());
            orderListCustomer.setContactNumber(ordersEntity.getCustomer().getContactNumber());
            orderListCustomer.setEmailAddress(ordersEntity.getCustomer().getEmail());

            OrderListAddressState orderListAddressState = new OrderListAddressState();
            orderListAddressState.setId(UUID.fromString(ordersEntity.getAddress().getState().getUuid()));
            orderListAddressState.setStateName(ordersEntity.getAddress().getState().getState_name());

            OrderListAddress orderListAddress = new OrderListAddress();
            orderListAddress.setId(UUID.fromString(ordersEntity.getAddress().getUuid()));
            orderListAddress.setFlatBuildingName(ordersEntity.getAddress().getFlat_buil_number());
            orderListAddress.setLocality(ordersEntity.getAddress().getLocality());
            orderListAddress.setCity(ordersEntity.getAddress().getCity());
            orderListAddress.setPincode(ordersEntity.getAddress().getPincode());
            orderListAddress.setState(orderListAddressState);

            OrderListCoupon orderListCoupon = new OrderListCoupon();
            orderListCoupon.setId(UUID.fromString(ordersEntity.getCoupon().getUuid()));
            orderListCoupon.setCouponName(ordersEntity.getCoupon().getCoupon_name());
            orderListCoupon.setPercent(ordersEntity.getCoupon().getPercent());

            OrderListPayment orderListPayment = new OrderListPayment();
            orderListPayment.setId(UUID.fromString(ordersEntity.getUuid()));
            orderListPayment.setPaymentName(ordersEntity.getPayment().getPayment_name());

            OrderList orderList = new OrderList();
            orderList.setId(UUID.fromString(ordersEntity.getUuid()));
            orderList.setDate(ordersEntity.getDate().toString());
            orderList.setAddress(orderListAddress);
            orderList.setCustomer(orderListCustomer);
            orderList.setPayment(orderListPayment);
            orderList.setCoupon(orderListCoupon);
            orderList.setBill(ordersEntity.getBill());
            orderList.setDiscount(ordersEntity.getDiscount());

            for (OrderItemEntity orderItemEntity : itemBusinessService.getItemsByOrder(ordersEntity)) {

                ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
                itemQuantityResponseItem.setId(UUID.fromString(orderItemEntity.getItem().getUuid()));
                itemQuantityResponseItem.setItemName(orderItemEntity.getItem().getItem_name());
                itemQuantityResponseItem.setItemPrice(orderItemEntity.getItem().getPrice());
                //itemQuantityResponseItem.setType(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().toString()));

                ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
                itemQuantityResponse.setItem(itemQuantityResponseItem);
                itemQuantityResponse.setPrice(orderItemEntity.getPrice());
                itemQuantityResponse.setQuantity(orderItemEntity.getQuantity());

                orderList.addItemQuantitiesItem(itemQuantityResponse);
            }

            customerOrderResponse.addOrdersItem(orderList);

        }

        return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST,path="/order",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,@RequestBody(required = false) final SaveOrderRequest orderRequest)
    {
        CustomerAuthEntity customerAuthEntity =
                customerBusinessService.getCustomerByAuthToken(authorization);
        CustomerEntity customerEntity = customerAuthEntity.getCustomer();
        OrdersEntity order = new OrdersEntity();
        CouponEntity couponEntity = orderBusinessService.getCouponByCouponUUID(orderRequest.getCouponId().toString());
        AddressEntity addressEntity = addressBusinessService.getAddressByUUID(orderRequest.getAddressId());
        CustomerAddressEntity customerAddressEntity = customerBusinessService.getCustAddressByAddressId(addressEntity.getId());
        PaymentEntity paymentEntity = paymentBusinessService.getPaymentBYUUID(orderRequest.getPaymentId().toString());
        RestaurantEntity restaurantEntity = restaurantBusinessService.getRestaurantByUUID(orderRequest.getRestaurantId().toString());
        order.setDate(ZonedDateTime.now());
        order.setRestaurant(restaurantEntity);
        order.setBill(orderRequest.getBill());
        order.setDiscount(orderRequest.getDiscount());
        order.setAddress(addressEntity);
        order.setCoupon(couponEntity);
        order.setPayment(paymentEntity);
        order.setUuid(UUID.randomUUID().toString());
        order.setCustomer(customerEntity);
        OrdersEntity savedOrder = orderBusinessService.saveOrder(order);

        for(ItemQuantity item : orderRequest.getItemQuantities())
        {
            ItemEntity itemEntity = itemBusinessService.getItemByUUID(item.getItemId().toString());
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrder(savedOrder);
            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setPrice(item.getPrice());
            orderItemEntity.setQuantity(item.getQuantity());
            orderBusinessService.saveOrderItem(orderItemEntity);
        }
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse().id(UUID.fromString(order.getUuid()).toString()).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse,HttpStatus.CREATED);
    }

}
