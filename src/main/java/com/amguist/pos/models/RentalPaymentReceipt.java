package com.amguist.pos.models;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class RentalPaymentReceipt {
    private String  toolCode;
    private String  toolType;
    private String  toolBrand;

    private Integer rentalDayCount;
    private String  checkoutDate;
    private String  dueDate;

    private Double  dailyRentalCharge;
    private Integer chargeDays;

    private Double  preDiscountCharge;
    private Integer discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

    private List<String> violations = new ArrayList<>();

    public void addViolation(String violation) {
        this.violations.add(violation);
    }
    public void printViolations() {
        violations.forEach(log::info);
    }

    public void printReceipt() {
        log.info("************* Receipt *************");
        log.info("Tool code: {}", this.toolCode);
        log.info("Tool type: {}", this.toolType);
        log.info("Tool brand: {}", this.toolBrand);
        log.info("Checkout Date: {}", this.checkoutDate);
        log.info("Due Date: {}", this.dueDate);
        log.info("Rental Day Count: {}", this.rentalDayCount);
        log.info("Daily Charge: ${}", this.dailyRentalCharge);
        log.info("Total Number Of Days Charged: {}", this.chargeDays);

        log.info("*****");
        log.info("Pre Discount ( SubTotal ): ${}", this.preDiscountCharge);
        log.info("Discount Percentage: {}%", this.discountPercentage);
        log.info("Discount Amount: ${}", this.discountAmount);
        log.info("Final charge: ${}", this.finalCharge);
    }

}
