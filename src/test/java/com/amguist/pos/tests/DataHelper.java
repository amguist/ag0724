package com.amguist.pos.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Slf4j
public class DataHelper {

    private static final ObjectMapper   MAPPER;

    static {
        MAPPER = new ObjectMapper();

        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        MAPPER.setDateFormat(df);
    }

    public static <T> T readValueFromResource(String resource, Class<T> someClass) {
        try {
            return (T) MAPPER.readValue(new File(resource), someClass);
        } catch (IOException iX) {
            log.error("Exception has occurred while trying to read in the resource ...");
            return null;
        }
    }

    public static String asJsonString(final Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception eX) {
            return null;
        }
    }
}
