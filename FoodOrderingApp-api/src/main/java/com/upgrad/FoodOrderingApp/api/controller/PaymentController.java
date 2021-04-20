package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.PaymentBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class PaymentController {

    @Autowired
    private PaymentBusinessService paymentBusinessService;

    /*
        This endpoint is used to fetch all the categories.
        Any user can access this endpoint.
     */
    @RequestMapping(path = "/payment", method = RequestMethod.GET, produces =
            MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getAllMethods() {

        List<PaymentEntity> paymentMethods = paymentBusinessService.getAllMethods();

        List<PaymentResponse> list = new ArrayList<PaymentResponse>();

        for(int i = 0; i < paymentMethods.size(); i++) {
            PaymentResponse payment = new PaymentResponse();
            payment.id(UUID.fromString(paymentMethods.get(i).getUuid()))
                    .paymentName(paymentMethods.get(i).getPayment_name());
            list.add(payment);
        }

        PaymentListResponse paymentListResponse = new PaymentListResponse();
        paymentListResponse.paymentMethods(list);
        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);
    }

}
