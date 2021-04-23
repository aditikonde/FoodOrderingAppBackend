package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentBusinessService {

    @Autowired
    private PaymentDao paymentDao;

    @Transactional
    public List<PaymentEntity> getAllMethods() {
        return paymentDao.getPaymentMethods();
    }

    public PaymentEntity getPaymentBYUUID(String paymentId) {
        return paymentDao.getPaymentByUUID(paymentId);
    }
}
