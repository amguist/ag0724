package com.amguist.pos;

import com.amguist.pos.persistence.Inventory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class PointOfSaleApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PointOfSaleApplication.class,args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        Inventory.getInstance();
        return builder.sources(PointOfSaleApplication.class);
    }

}
