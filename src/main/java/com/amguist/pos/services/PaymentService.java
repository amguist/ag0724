package com.amguist.pos.services;

import com.amguist.pos.models.RentalPaymentReceipt;
import com.amguist.pos.models.RentalPaymentRequest;
import com.amguist.pos.models.ToolType;
import com.amguist.pos.persistence.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.DayOfWeek.*;

@Service
@Slf4j
public class PaymentService {

    private static final String DATE_FORMAT_STR     =   "mm/dd/yy";
    private static final String EMPTY_STR           =   "";
    private static final String DECIMAL_FORMAT_STR  =   "#.##";

    private static final Set<DayOfWeek> WEEKEND     = EnumSet.of(SATURDAY,SUNDAY);

    public Optional<RentalPaymentReceipt> performCheckout(RentalPaymentRequest request) {
        RentalPaymentReceipt receipt = new RentalPaymentReceipt();
        assignRequestValuesToReceipt(receipt, request);
        assignToolValuesToReceipt(receipt, request);
        return Optional.of(receipt);
    }

    private String calculateDueDate(RentalPaymentRequest request) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STR);
            Calendar c = Calendar.getInstance();
            c.setTime(dateFormat.parse(request.getCheckoutDate()));
            c.add(Calendar.DATE, request.getRentalDayCount());
            return dateFormat.format(c.getTime());
        } catch (Exception eX) {
            return EMPTY_STR;
        }
    }

    private void assignToolValuesToReceipt(RentalPaymentReceipt receipt, RentalPaymentRequest request) {
        var tool = Inventory.getInstance().getInventoryItem(request.getToolCode());
        var toolType = tool.getToolType();

        receipt.setToolType(toolType.getTypeName());
        receipt.setToolBrand(tool.getToolBrand());
        receipt.setDailyRentalCharge(toolType.getDailyCharge());

        var dueDate = calculateDueDate(request);
        receipt.setDueDate(dueDate);

        var chargedDays = calculateChargeDays(request, dueDate, toolType);
        receipt.setChargeDays(chargedDays);

        var preDiscountCharge = (calculatePreChargedAmount(chargedDays, toolType.getDailyCharge()));
        receipt.setPreDiscountCharge(preDiscountCharge);

        var discountAmount = calculateDiscountAmount(preDiscountCharge, receipt.getDiscountPercentage());
        receipt.setDiscountAmount(discountAmount);

        receipt.setFinalCharge(calculateFinalCharge(preDiscountCharge, discountAmount));
    }

    private BigDecimal calculateFinalCharge(Double preDiscountCharge, BigDecimal discountAmount) {
        BigDecimal finalCharge = BigDecimal.valueOf(preDiscountCharge - discountAmount.doubleValue());
        finalCharge = finalCharge.setScale(2, RoundingMode.HALF_UP);
        return finalCharge;
    }

    private BigDecimal calculateDiscountAmount(Double preDiscountCharge, double discountPercentage) {
        double discountFraction = discountPercentage/100;
        BigDecimal discountAmount = BigDecimal.valueOf(preDiscountCharge * discountFraction);
        discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        return discountAmount;
    }

    private Double calculatePreChargedAmount(Integer chargedDays, Double toolRentalRate) {
        BigDecimal bd = BigDecimal.valueOf(toolRentalRate * chargedDays);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Integer calculateChargeDays(RentalPaymentRequest request, String dueDate, ToolType toolType) {
        try {
            LocalDate startDate = LocalDate.parse(request.getCheckoutDate(), DateTimeFormatter.ofPattern("MM/dd/yy"));
            LocalDate endDate = LocalDate.parse(dueDate, DateTimeFormatter.ofPattern("MM/dd/yy"));

            var totalNumberOfDays = startDate.datesUntil(endDate).count();

            // Need to add a single day to the end date in this case as the datesUntil does not include the end date
            var numberOfDaysOnWeekend = startDate.datesUntil(endDate.plusDays(1)).filter(currentDate -> WEEKEND.contains(currentDate.getDayOfWeek())).count();

            // Exclude Weekends For Specific Type
            if(!toolType.getWeekendCharge()) {
                totalNumberOfDays -= numberOfDaysOnWeekend;
            }

            if(!toolType.getHolidayCharge()) {
                if(shouldExcludeFridayBeforeHoliday(startDate, endDate)) {
                    totalNumberOfDays--;
                }

                if(shouldExcludeMondayFollowingHoliday(startDate, endDate)) {
                    totalNumberOfDays--;
                }
            }

            return Math.toIntExact(totalNumberOfDays);
        } catch (Exception eX) {
            return null;
        }
    }

    private boolean shouldExcludeMondayFollowingHoliday(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1)).allMatch(localDate -> {
            if(localDate.getDayOfWeek().equals(SUNDAY) && localDate.getMonth().equals(Month.JULY) && localDate.getDayOfMonth() == 4) {
                return true;
            }
            return false;
        });
    }

    private boolean shouldExcludeFridayBeforeHoliday(LocalDate startDate, LocalDate endDate) {
        // This will only be applicable for July 4th
        AtomicBoolean shouldRemoveDay = new AtomicBoolean(false);
        startDate.datesUntil(endDate.plusDays(1)).forEach(localDate -> {
            if(localDate.getDayOfWeek().equals(SATURDAY) && localDate.getMonth().equals(Month.JULY) && localDate.getDayOfMonth() == 4) {
                shouldRemoveDay.set(true);
            }
        });
        return shouldRemoveDay.get();
    }

    private void assignRequestValuesToReceipt(RentalPaymentReceipt receipt, RentalPaymentRequest request) {
        receipt.setToolCode(request.getToolCode());
        receipt.setRentalDayCount(request.getRentalDayCount());
        receipt.setDiscountPercentage(request.getDiscountPercentage());
        receipt.setCheckoutDate(request.getCheckoutDate());
    }
}
