package com.amguist.pos.tests.controllers;

import com.amguist.pos.controllers.PaymentController;
import com.amguist.pos.models.RentalPaymentReceipt;
import com.amguist.pos.models.RentalPaymentRequest;
import com.amguist.pos.persistence.Inventory;
import com.amguist.pos.services.PaymentService;
import com.amguist.pos.tests.DataHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;
import java.util.Optional;

import static com.amguist.pos.tests.DataHelper.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PaymentController.class)
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc             mockMvc;

    @InjectMocks
    private PaymentController   mockPaymentController;

    @MockBean
    private PaymentService      paymentService;

    @Before
    public void init() {
        Inventory.getInstance();

        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockPaymentController).build();
    }

    @Test
    public void whenSuppliedDiscountOver100Percent() throws Exception {
        // Data Mock
        RentalPaymentRequest requestBody = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_1.json", RentalPaymentRequest.class);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/checkout")
            .content(Objects.requireNonNull(asJsonString(requestBody)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andReturn();
    }

    @Test
    public void whenSuppliedValidLadderPurchase() throws Exception {
        RentalPaymentRequest requestBody = DataHelper.readValueFromResource("src/test/resources/rentals/rental_data_2.json", RentalPaymentRequest.class);

        // Given
        RentalPaymentReceipt receipt = new RentalPaymentReceipt();
        receipt.setToolCode(requestBody.getToolCode());
        receipt.setRentalDayCount(requestBody.getRentalDayCount());
        receipt.setDiscountPercentage(requestBody.getDiscountPercentage());
        receipt.setCheckoutDate(requestBody.getCheckoutDate());

        Optional<RentalPaymentReceipt> rentalPaymentReceipt = Optional.of(receipt);

        given(paymentService.performCheckout(any())).willReturn(rentalPaymentReceipt);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/checkout")
            .content(Objects.requireNonNull(asJsonString(requestBody)))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
    }
}
