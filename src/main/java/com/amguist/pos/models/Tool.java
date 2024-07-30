package com.amguist.pos.models;

import lombok.Data;

@Data
public class Tool {

    private String      toolCode;
    private ToolType    toolType;
    private String      toolBrand;

}
