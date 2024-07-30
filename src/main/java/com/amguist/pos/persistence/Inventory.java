package com.amguist.pos.persistence;

import com.amguist.pos.models.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class Inventory {

    private final List<Tool>      toolInventory;
    private final ObjectMapper    objectMapper;
    private static Inventory      instance;

    private static final String INVENTORY_RESOURCE = "src/main/resources/data/inventory.json";

    private Inventory() {
        objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        objectMapper.setDateFormat(df);

        toolInventory = new ArrayList<>();
        loadInventory();
    }

    private void loadInventory() {
        try {
           toolInventory.addAll(objectMapper.readerForListOf(Tool.class).readValue(new File(INVENTORY_RESOURCE)));
        } catch (IOException iX) {
            log.error("Unable to load inventory due to the following error: ", iX);
        }
    }

    public Tool getInventoryItem(final String toolCode) {
        return toolInventory.stream()
            .filter(tool -> tool.getToolCode().equals(toolCode))
            .findFirst().orElse(null);
    }

    public static Inventory getInstance() {
        if(Objects.isNull(instance)) {
            instance = new Inventory();
        }
        return instance;
    }
}
