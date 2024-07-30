package com.amguist.pos.models;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RentalPaymentRequest {

    @NotBlank
    private String  toolCode;

    @NotNull
    @Min(value = 1, message = "Rental Days Must Greater Than 0")
    private Integer rentalDayCount;

    @NotNull
    @Min(value = 0, message = "Discount Percentage Must Be Between 0 and 100")
    @Max(value = 100, message = "Discount Percentage Must Be Between 0 and 100")
    private Integer discountPercentage;

    @NotBlank
    private String  checkoutDate;

}
