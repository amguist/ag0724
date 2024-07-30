package com.amguist.pos.controllers;

import com.amguist.pos.models.RentalPaymentReceipt;
import com.amguist.pos.models.RentalPaymentRequest;
import com.amguist.pos.services.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/checkout")
    public ResponseEntity<RentalPaymentReceipt> checkout(@Valid @RequestBody RentalPaymentRequest request) {
        return paymentService.performCheckout(request)
            .map(rentalPaymentReceipt -> new ResponseEntity<>(rentalPaymentReceipt, HttpStatus.OK))
            .orElseGet(() -> ResponseEntity.internalServerError().build());
    }
}
