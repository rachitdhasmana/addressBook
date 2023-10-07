package com.reeceTech.addressBook;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info =
        @Info(
            title = "AddressBook",
            version = "1",
            description = "An AddressBook implementation using SpringBoot"))
public class AddressBookApplication {

  public static void main(String[] args) {
    SpringApplication.run(AddressBookApplication.class, args);
  }
}
