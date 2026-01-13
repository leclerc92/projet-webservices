package com.micheldev.soappolicysercice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.micheldev.soappolicysercice", "policyService"})
public class SoapPolicySerciceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoapPolicySerciceApplication.class, args);
    }

}
