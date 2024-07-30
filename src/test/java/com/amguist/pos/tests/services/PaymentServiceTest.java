package com.amguist.pos.tests.services;

import com.amguist.pos.models.RentalPaymentReceipt;
import com.amguist.pos.models.RentalPaymentRequest;
import com.amguist.pos.persistence.Inventory;
import com.amguist.pos.services.PaymentService;
import com.amguist.pos.tests.DataHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@Slf4j
@FixMethodOrder(MethodSorters.JVM)
public class PaymentServiceTest {

    private PaymentService  paymentService;

    @Before
    public void init() {
        // We will just initialize the inventory cache so that we can do all the necessary lookups
        Inventory.getInstance();
        paymentService = new PaymentService();
    }

    @Test
    public void whenRequestHasValidationErrorTest() {
        log.info("");
        RentalPaymentRequest request = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_1.json", RentalPaymentRequest.class);
        var paymentResult = paymentService.performCheckout(request);
        log.info("****** TEST 1 *******");
        paymentResult.ifPresent(RentalPaymentReceipt::printViolations);
        log.info("");
    }

    @Test
    public void whenToolIsLadderTest() {
        log.info("");
        RentalPaymentRequest request = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_2.json", RentalPaymentRequest.class);
        var paymentResult = paymentService.performCheckout(request);
        log.info("****** TEST 2 *******");
        paymentResult.ifPresent(RentalPaymentReceipt::printReceipt);
        log.info("");
    }

    @Test
    public void whenToolIsChainsawTest() {
        log.info("");
        RentalPaymentRequest request = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_3.json", RentalPaymentRequest.class);
        var paymentResult = paymentService.performCheckout(request);
        log.info("****** TEST 3 *******");
        paymentResult.ifPresent(RentalPaymentReceipt::printReceipt);
        log.info("");
    }

    @Test
    public void whenToolIsJackhammerDeWaltLaborDayTest() {
        log.info("");
        RentalPaymentRequest request = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_4.json", RentalPaymentRequest.class);
        var paymentResult = paymentService.performCheckout(request);
        log.info("****** TEST 4 *******");
        paymentResult.ifPresent(RentalPaymentReceipt::printReceipt);
        log.info("");
    }

    @Test
    public void whenToolIsJackhammerRidgidDayTest() {
        log.info("");
        RentalPaymentRequest request = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_5.json", RentalPaymentRequest.class);
        var paymentResult = paymentService.performCheckout(request);
        log.info("****** TEST 5 *******");
        paymentResult.ifPresent(RentalPaymentReceipt::printReceipt);
        log.info("");
    }

    @Test
    public void whenToolIsJackhammerRidgidHolidayTest() {
        log.info("");
        RentalPaymentRequest request = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_6.json", RentalPaymentRequest.class);
        var paymentResult = paymentService.performCheckout(request);
        log.info("****** TEST 6 *******");
        paymentResult.ifPresent(RentalPaymentReceipt::printReceipt);
        log.info("");
    }
}
