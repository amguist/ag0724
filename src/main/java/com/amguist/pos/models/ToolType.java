package com.amguist.pos.models;

import lombok.Data;

@Data
public class ToolType {

    private String  typeName;
    private Double  dailyCharge;
    private Boolean weekdayCharge;
    private Boolean weekendCharge;
    private Boolean holidayCharge;

}
